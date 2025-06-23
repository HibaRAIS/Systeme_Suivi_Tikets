package com.example.systeme_suivi_ticket.service.impl;

import com.example.systeme_suivi_ticket.dto.*;
import com.example.systeme_suivi_ticket.model.*;
import com.example.systeme_suivi_ticket.repository.*;
import com.example.systeme_suivi_ticket.service.EmailService;
import com.example.systeme_suivi_ticket.service.TicketService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketPriorityRepository ticketPriorityRepository;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Autowired
    private TicketStatusRepository ticketStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired private EmailService emailService;

    @Autowired
    private TicketCommentRepository ticketCommentRepository;

    @Autowired
    private TicketStatusHistoryRepository ticketStatusHistoryRepository;


    @Override
    public List<TicketStatus> getAllStatuses() {
        return ticketStatusRepository.findAll();
    }

    @Override
    public long countAllTickets() {
        return ticketRepository.count();
    }

    @Override
    public long countByStatus(TicketStatus status) {
        return ticketRepository.countByStatus(status);
    }

    @Override
    public List<Ticket> findRecentTicketsByUser(User user) {
        return ticketRepository.findTop5ByCreatedByOrderByCreatedAtDesc(user);
    }

    @Override
    public List<Ticket> findTicketsAssignedTo(User technician) {
        return ticketRepository.findByAssignedToOrderByCreatedAtDesc(technician);
    }

    @Override
    public void createTicket(String title, String description, Integer priorityId, Integer typeId, User createdBy) {
        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setCreatedBy(createdBy);
        ticket.setCreatedAt(LocalDateTime.now());

        ticket.setPriority(ticketPriorityRepository.findById(priorityId).orElse(null));
        ticket.setType(ticketTypeRepository.findById(typeId).orElse(null));
        // ticket.setStatus(ticketStatusRepository.findDefaultStatus()); // Assume
        // default is "Open"

        ticketRepository.save(ticket);
    }

    @Override
    public void deleteTicketById(Long id) {
        ticketRepository.deleteById(id);
    }

    @Override
    public void updateTicket(Ticket ticket) {
        ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void assignTicket(AssignTicketRequest request) {
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User technician = userRepository.findById(request.getTechnicianId())
                .orElseThrow(() -> new RuntimeException("Technician not found"));

        ticket.setAssignedTo(technician);
        ticket.setAssignmentNote(request.getAssignmentNote());
        ticket.setStatus(ticketStatusRepository.findById(2).orElseThrow()); // IN_PROGRESS

        ticketRepository.save(ticket);

        if (request.isNotifyTechnician()) {
            emailService.sendAssignmentNotification(ticket);
        }
    }



    //admin dashboard
    @Override
    public long countOpenTickets() {
        // Nous partons du principe que le statut "Open" a l'ID 1 dans votre base de données.
        // C'est une convention courante.
        TicketStatus openStatus = ticketStatusRepository.findById(1)
                .orElse(null); // Retourne null si le statut n'est pas trouvé

        if (openStatus != null) {
            return ticketRepository.countByStatus(openStatus);
        }

        return 0; // Retourne 0 si le statut "Open" n'existe pas pour éviter les erreurs.
    }


    @Override
    public long countResolvedTickets() {
        //  le statut "Resolved" a l'ID 3
        TicketStatus resolvedStatus = ticketStatusRepository.findById(3).orElse(null);
        if (resolvedStatus != null) {
            return ticketRepository.countByStatus(resolvedStatus);
        }
        return 0;
    }

    @Override
    public long countUnassignedTickets() {
        return ticketRepository.countByAssignedToIsNull();
    }

    @Override
    public Map<String, Long> getTicketStatusDistribution() {
        // LinkedHashMap conserve l'ordre d'insertion, ce qui peut être utile.
        Map<String, Long> distribution = new LinkedHashMap<>();

        List<Object[]> results = ticketRepository.countTicketsByStatus();

        for (Object[] result : results) {
            String statusName = (String) result[0];
            Long count = (Long) result[1];
            distribution.put(statusName, count);
        }

        return distribution;
    }

    @Override
    public List<Ticket> findRecentTickets() {
        return ticketRepository.findTop5ByOrderByCreatedAtDesc();
    }

    @Override
    public ChartDataDTO getMonthlyTicketCreationStats() {
        // 1. Définir la période : les 12 derniers mois
        LocalDateTime twelveMonthsAgo = LocalDateTime.now().minusMonths(11).withDayOfMonth(1).toLocalDate().atStartOfDay();

        // 2. Récupérer les données brutes de la base de données
        List<Object[]> results = ticketRepository.findMonthlyTicketCreationCounts(twelveMonthsAgo);

        // 3. Convertir les résultats en une Map pour un accès facile
        Map<String, Long> monthlyCounts = results.stream()
                .collect(Collectors.toMap(
                        r -> r[0] + "-" + r[1], // Clé ex: "2024-6"
                        r -> (Long) r[2]
                ));

        // 4. Préparer les listes finales pour les 12 mois
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        LocalDateTime dateIterator = LocalDateTime.now();
        for (int i = 0; i < 12; i++) {
            Month month = dateIterator.minusMonths(i).getMonth();
            int year = dateIterator.minusMonths(i).getYear();

            // On ajoute le label du mois (ex: "Jun") au début de la liste
            labels.add(0, month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));

            // On cherche le compte pour ce mois, on met 0L si on ne le trouve pas
            String key = year + "-" + month.getValue();
            data.add(0, monthlyCounts.getOrDefault(key, 0L));
        }

        return new ChartDataDTO(labels, data);
    }

    @Override
    public ChartDataDTO getMonthlyTicketResolutionStats() {
        LocalDateTime twelveMonthsAgo = LocalDateTime.now().minusMonths(11).withDayOfMonth(1).toLocalDate().atStartOfDay();
        List<Object[]> results = ticketRepository.findMonthlyTicketResolutionCounts(twelveMonthsAgo);

        // Le reste de la logique pour transformer les résultats en DTO est identique
        Map<String, Long> monthlyCounts = results.stream()
                .collect(Collectors.toMap(r -> r[0] + "-" + r[1], r -> (Long) r[2]));

        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        LocalDateTime dateIterator = LocalDateTime.now();
        for (int i = 0; i < 12; i++) {
            Month month = dateIterator.minusMonths(i).getMonth();
            int year = dateIterator.minusMonths(i).getYear();
            labels.add(0, month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            String key = year + "-" + month.getValue();
            data.add(0, monthlyCounts.getOrDefault(key, 0L));
        }
        return new ChartDataDTO(labels, data);
    }

    @Override
    public ChartDataDTO getTicketStatsByPeriod(String period, String type) {
        LocalDateTime startDate;
        List<Object[]> results;

        switch (period.toLowerCase()) {
            case "week":
                startDate = LocalDateTime.now().minusDays(6).toLocalDate().atStartOfDay();
                results = ticketRepository.findDailyTicketCreationCounts(startDate);
                // On passe directement TextStyle.SHORT ici
                return formatDailyData(results, 7, TextStyle.SHORT);

            case "year":
                startDate = LocalDateTime.now().minusYears(4).withDayOfYear(1).toLocalDate().atStartOfDay();
                results = ticketRepository.findYearlyTicketCreationCounts(startDate);
                return formatYearlyData(results, 5);

            default: // "month"
                startDate = LocalDateTime.now().minusMonths(11).withDayOfMonth(1).toLocalDate().atStartOfDay();
                if ("resolved".equals(type)) {
                    results = ticketRepository.findMonthlyTicketResolutionCounts(startDate);
                } else {
                    results = ticketRepository.findMonthlyTicketCreationCounts(startDate);
                }
                return formatMonthlyData(results, 12);
        }
    }

    // === DES MÉTHODES PRIVÉES POUR FORMATER LES DONNÉES ===
    private ChartDataDTO formatMonthlyData(List<Object[]> results, int months) {
        Map<String, Long> dbCounts = results.stream().collect(Collectors.toMap(r -> r[0] + "-" + r[1], r -> (Long) r[2]));
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        for (int i = months - 1; i >= 0; i--) {
            LocalDateTime date = LocalDateTime.now().minusMonths(i);
            // CORRECTION : On utilise TextStyle.SHORT
            labels.add(date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            String key = date.getYear() + "-" + date.getMonthValue();
            data.add(dbCounts.getOrDefault(key, 0L));
        }
        return new ChartDataDTO(labels, data);
    }

    private ChartDataDTO formatDailyData(List<Object[]> results, int days, TextStyle style) {
        Map<LocalDate, Long> dbCounts = results.stream().collect(Collectors.toMap(r -> ((java.sql.Date) r[0]).toLocalDate(), r -> (Long) r[1]));
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            // Cette ligne est maintenant correcte car 'style' est bien un TextStyle
            labels.add(date.getDayOfWeek().getDisplayName(style, Locale.ENGLISH));
            data.add(dbCounts.getOrDefault(date, 0L));
        }
        return new ChartDataDTO(labels, data);
    }

    private ChartDataDTO formatYearlyData(List<Object[]> results, int years) {
        Map<Integer, Long> dbCounts = results.stream().collect(Collectors.toMap(r -> (Integer) r[0], r -> (Long) r[1]));
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        for (int i = years - 1; i >= 0; i--) {
            int year = LocalDate.now().minusYears(i).getYear();
            labels.add(String.valueOf(year));
            data.add(dbCounts.getOrDefault(year, 0L));
        }
        return new ChartDataDTO(labels, data);
    }

    //interface user
    @Override
    public List<Ticket> filterTickets(Long userId, Integer statusId, Integer priorityId, Integer typeId,
                                      String search) {
        boolean isUserIdDefault = (userId == null || userId == 0);
        boolean isStatusIdDefault = (statusId == null || statusId == 0);
        boolean isPriorityIdDefault = (priorityId == null || priorityId == 0);
        boolean isTypeIdDefault = (typeId == null || typeId == 0);
        boolean isSearchEmpty = (search == null || search.isEmpty());

        if (isUserIdDefault && isStatusIdDefault && isPriorityIdDefault && isTypeIdDefault && isSearchEmpty) {
            return ticketRepository.findAllByCreatedById(userId);
        }

        return ticketRepository.findTicketsWithFilters(userId, statusId, priorityId, typeId, search);
    }

    //technicien
    @Override
    public TechnicianDashboardStatsDTO getTechnicianDashboardStats(User technician) {
        // 1. Récupérer tous les tickets assignés à ce technicien
        List<Ticket> assignedTickets = ticketRepository.findByAssignedToOrderByCreatedAtDesc(technician);

        // 2. Initialiser les compteurs
        long resolvedCount = 0;
        long inProgressCount = 0;
        long highPriorityCount = 0;
        long unresolvedCount = 0;

        // 3. Parcourir les tickets pour calculer les statistiques
        for (Ticket ticket : assignedTickets) {
            // --- Comparaison par ID (plus robuste) ---

            // Compter les tickets résolus (StatusID = 3)
            if (ticket.getStatus() != null && ticket.getStatus().getStatusId() == 3) {
                resolvedCount++;
            }

            // Compter les tickets en cours (StatusID = 2)
            if (ticket.getStatus() != null && ticket.getStatus().getStatusId() == 2) {
                inProgressCount++;
            }

            // Un ticket non résolu est un ticket dont le statut n'est NI Résolu (3) NI Fermé (4)
            if (ticket.getStatus() != null && ticket.getStatus().getStatusId() != 3 && ticket.getStatus().getStatusId() != 4) {
                unresolvedCount++;
            }

            // Compter les tickets à haute priorité (PriorityID = 3)
            if (ticket.getPriority() != null && ticket.getPriority().getPriorityId() == 3) {
                highPriorityCount++;
            }
        }

        // 4. Créer et remplir le DTO de retour
        TechnicianDashboardStatsDTO stats = new TechnicianDashboardStatsDTO();
        // Utilise le prénom et le nom s'ils existent, sinon le username
        String techFullName = (technician.getFirstName() != null && !technician.getFirstName().isEmpty())
                ? technician.getFirstName() + " " + technician.getLastName()
                : technician.getUsername();

        stats.setTechnicianName(techFullName);
        stats.setTotalTickets(assignedTickets.size());
        stats.setResolvedCount(resolvedCount);
        stats.setInProgressCount(inProgressCount);
        stats.setHighPriorityCount(highPriorityCount);
        stats.setUnresolvedCount(unresolvedCount);

        return stats;
    }

    @Override
    public List<Ticket> findTicketsAssignedToWithSearch(User technician, String keyword) {
        // Si le mot-clé est nul ou vide, on renvoie tous les tickets assignés.
        if (keyword == null || keyword.trim().isEmpty()) {
            return ticketRepository.findByAssignedToOrderByCreatedAtDesc(technician);
        }
        // Sinon, on appelle la nouvelle méthode de recherche.
        return ticketRepository.searchAssignedTickets(technician, keyword);
    }

    @Override
    public List<Ticket> findSortedTicketsAssignedTo(User technician, String sortField, String sortDir) {
        // Crée un objet de tri basé sur les paramètres.
        // Si la direction est "asc", tri ascendant, sinon descendant.
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        // Appelle la nouvelle méthode du repository
        return ticketRepository.findByAssignedTo(technician, sort);
    }

    @Override
    public List<Ticket> findAndFilterTickets(User technician, String statusFilter, String keyword, String sortField, String sortDir) {
        // Cette méthode utilise DÉJÀ votre méthode findAndFilterAssignedTickets.
        // Aucune modification n'est nécessaire ici.
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        List<Integer> statusIds = null;
        if (statusFilter != null && !statusFilter.isEmpty()) {
            switch (statusFilter) {
                case "active":
                    statusIds = List.of(1, 2);
                    break;
                case "pending":
                    statusIds = List.of(2); // Ou votre logique pour "Pending"
                    break;
                case "resolved":
                    statusIds = List.of(3);
                    break;
            }
        }

        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        return ticketRepository.findAndFilterAssignedTickets(technician, searchKeyword, statusIds, sort);
    }

    //details tickets
    @Override
    @Transactional
    public TicketDetailDTO getTicketDetailsById(Long ticketId) {
        // La première partie de votre méthode ne change pas
        Ticket ticket = ticketRepository.findById(ticketId) // NOTE: J'ai retiré "WithDetails" pour simplifier
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + ticketId));

        TicketDetailDTO dto = new TicketDetailDTO();
        dto.setTicketId(ticket.getTicketId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());

        // Formatage des dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        dto.setCreatedAt(ticket.getCreatedAt().format(formatter));
        dto.setUpdatedAt(ticket.getUpdatedAt().format(formatter));

        // Informations de statut, priorité, type avec classes CSS pour les badges
        dto.setStatus(ticket.getStatus().getStatusName());
        dto.setStatusClass(getStatusCssClass(ticket.getStatus().getStatusName()));
        dto.setPriority(ticket.getPriority().getPriorityName());
        dto.setPriorityClass(getPriorityCssClass(ticket.getPriority().getPriorityName()));
        dto.setType(ticket.getType().getTypeName());

        // Créateur du ticket
        dto.setCreatedBy(createUserSummary(ticket.getCreatedBy()));

        // Technicien assigné (peut être null)
        if (ticket.getAssignedTo() != null) {
            dto.setAssignedTo(createUserSummary(ticket.getAssignedTo()));
        }

        // Liste des commentaires (votre code existant)
        dto.setComments(ticket.getComments().stream()
                .map(comment -> new CommentDTO(
                        getFullName(comment.getUser()), // Utilise la nouvelle méthode
                        getInitials(comment.getUser()),
                        comment.getComment(),
                        comment.getCreatedAt().format(formatter)
                ))
                .collect(Collectors.toList()));

        // Update
        dto.setStatusId(ticket.getStatus().getStatusId());
        dto.setPriorityId(ticket.getPriority().getPriorityId());


        // ===================================================================
        //  AJOUT DE LA LOGIQUE SIMPLIFIÉE POUR L'HISTORIQUE
        // ===================================================================
        List<HistoryEntryDTO> historyEntries = new ArrayList<>();

        // A. Événement de création du ticket
        historyEntries.add(new HistoryEntryDTO(
                "Ticket created",
                getFullName(ticket.getCreatedBy()),
                ticket.getCreatedAt().format(formatter),
                ticket.getCreatedAt()
        ));

        // B. Récupérer les changements de statut depuis la DB
        List<TicketStatusHistory> statusChanges = ticketStatusHistoryRepository.findByTicket_TicketIdOrderByChangedAtAsc(ticketId);
        for (TicketStatusHistory statusChange : statusChanges) {
            String description = "Status changed to \"" + statusChange.getStatus().getStatusName() + "\"";
            historyEntries.add(new HistoryEntryDTO(
                    description,
                    getFullName(statusChange.getChangedBy()),
                    statusChange.getChangedAt().format(formatter),
                    statusChange.getChangedAt()
            ));
        }

        // C. Récupérer les commentaires depuis la DB pour les ajouter aussi à l'historique
        // Note: on utilise la liste déjà chargée par Hibernate grâce à la relation
        for (TicketComment comment : ticket.getComments()) {
            String description = "Comment added: \"" + truncate(comment.getComment()) + "\"";
            historyEntries.add(new HistoryEntryDTO(
                    description,
                    getFullName(comment.getUser()),
                    comment.getCreatedAt().format(formatter),
                    comment.getCreatedAt()
            ));
        }

        // D. Trier la liste fusionnée par date (du plus récent au plus ancien)
        historyEntries.sort(Comparator.comparing(HistoryEntryDTO::getRawTimestamp).reversed());

        // E. Ajouter la liste triée au DTO final
        dto.setHistory(historyEntries);
        // ===================================================================
        // FIN DE LA LOGIQUE D'HISTORIQUE
        // ===================================================================

        return dto;
    }

// --- MÉTHODES UTILITAIRES À AJOUTER OU À VÉRIFIER ---

    // Votre méthode existante, légèrement modifiée pour utiliser getFullName
    private UserSummaryDTO createUserSummary(User user) {
        if (user == null) return null;
        return new UserSummaryDTO(getFullName(user), getInitials(user));
    }

    // Votre méthode existante, pas de changement
    private String getInitials(User user) {
        if (user == null) return "";
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
            return ("" + firstName.charAt(0) + lastName.charAt(0)).toUpperCase();
        }
        return user.getUsername().substring(0, Math.min(user.getUsername().length(), 2)).toUpperCase();
    }

    // NOUVELLE méthode utilitaire pour avoir le nom complet de manière cohérente
    private String getFullName(User user) {
        if (user == null) return "System"; // Cas où l'auteur n'est pas défini
        if (user.getFirstName() != null && !user.getFirstName().trim().isEmpty()) {
            return user.getFirstName() + " " + user.getLastName();
        }
        return user.getUsername();
    }

    // NOUVELLE méthode utilitaire pour raccourcir les longs commentaires dans l'historique
    private String truncate(String text) {
        if (text == null || text.length() <= 50) {
            return text;
        }
        return text.substring(0, 47) + "...";
    }

    // Vos méthodes pour les classes CSS, pas de changement
    private String getStatusCssClass(String statusName) {
        // ... votre code existant
        switch (statusName.toLowerCase()) {
            case "open": return "badge-open";
            case "in progress": return "badge-in-progress";
            case "resolved": return "badge-resolved";
            case "closed": return "badge-closed";
            default: return "bg-secondary";
        }
    }

    private String getPriorityCssClass(String priorityName) {
        // ... votre code existant
        switch (priorityName.toLowerCase()) {
            case "low": return "badge-low";
            case "medium": return "badge-medium";
            case "high": return "badge-high";
            default: return "bg-secondary";
        }


    }


    @Override
    @Transactional
    public void updateTicketStatusAndPriority(Long ticketId, Integer statusId, Integer priorityId, User changedBy) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + ticketId));

        boolean hasChanges = false;

        // 1. Vérifier et mettre à jour le statut
        if (statusId != null && !statusId.equals(ticket.getStatus().getStatusId())) {
            TicketStatus newStatus = ticketStatusRepository.findById(statusId)
                    .orElseThrow(() -> new EntityNotFoundException("Status with id " + statusId + " not found"));
            ticket.setStatus(newStatus);

            // !! POINT CLÉ : Enregistrer l'événement dans l'historique !!
            TicketStatusHistory historyEvent = new TicketStatusHistory(ticket, newStatus, changedBy);
            ticketStatusHistoryRepository.save(historyEvent);

            hasChanges = true;
        }

        // 2. Vérifier et mettre à jour la priorité
        if (priorityId != null && !priorityId.equals(ticket.getPriority().getPriorityId())) {
            TicketPriority newPriority = ticketPriorityRepository.findById(priorityId)
                    .orElseThrow(() -> new EntityNotFoundException("Priority with id " + priorityId + " not found"));
            ticket.setPriority(newPriority);
            hasChanges = true;
        }

        // 3. Sauvegarder le ticket seulement si des changements ont eu lieu
        if (hasChanges) {
            ticketRepository.save(ticket);
        }
    }

    @Override
    @Transactional
    public void addCommentToTicket(Long ticketId, String commentText, User author) {
        // 1. Valider les entrées
        if (commentText == null || commentText.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be empty.");
        }

        // 2. Récupérer le ticket auquel le commentaire sera attaché
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + ticketId));

        // 3. Créer la nouvelle entité Commentaire
        TicketComment newComment = new TicketComment();
        newComment.setTicket(ticket);
        newComment.setUser(author);
        newComment.setComment(commentText);
        newComment.setCreatedAt(LocalDateTime.now()); // La date est définie automatiquement, mais c'est bien de l'expliciter

        // 4. Sauvegarder le commentaire. Grâce à la cascade, le ticket sera aussi mis à jour (UpdatedAt)
        ticketCommentRepository.save(newComment);

        // Optionnel mais recommandé : Mettre à jour manuellement le champ UpdatedAt du ticket
        // car l'ajout d'un commentaire est une modification du ticket.
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
    }


}

