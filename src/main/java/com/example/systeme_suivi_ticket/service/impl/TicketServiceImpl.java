package com.example.systeme_suivi_ticket.service.impl;

import com.example.systeme_suivi_ticket.dto.AssignTicketRequest;
import com.example.systeme_suivi_ticket.dto.ChartDataDTO;
import com.example.systeme_suivi_ticket.model.Ticket;
import com.example.systeme_suivi_ticket.model.TicketStatus;
import com.example.systeme_suivi_ticket.model.User;
import com.example.systeme_suivi_ticket.repository.*;
import com.example.systeme_suivi_ticket.service.EmailService;
import com.example.systeme_suivi_ticket.service.TicketService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
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

}

