package com.example.systeme_suivi_ticket.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.systeme_suivi_ticket.dto.AssignTicketRequest;
import com.example.systeme_suivi_ticket.model.Ticket;
import com.example.systeme_suivi_ticket.model.TicketComment;
import com.example.systeme_suivi_ticket.model.TicketPriority;
import com.example.systeme_suivi_ticket.model.TicketStatus;
import com.example.systeme_suivi_ticket.model.TicketType;
import com.example.systeme_suivi_ticket.model.User;
import com.example.systeme_suivi_ticket.repository.TicketCommentRepository;
import com.example.systeme_suivi_ticket.repository.TicketPriorityRepository;
import com.example.systeme_suivi_ticket.repository.TicketRepository;
import com.example.systeme_suivi_ticket.repository.TicketStatusRepository;
import com.example.systeme_suivi_ticket.repository.TicketTypeRepository;
import com.example.systeme_suivi_ticket.repository.UserRepository;
import com.example.systeme_suivi_ticket.service.DashboardService;
import com.example.systeme_suivi_ticket.service.TicketService;
import com.example.systeme_suivi_ticket.service.UserService;

@Controller
public class TicketController {

	private final UserRepository userRepository;
	private final TicketRepository ticketRepository;
	private final TicketPriorityRepository ticketPriorityRepository;
	private final TicketTypeRepository ticketTypeRepository;
	private final TicketStatusRepository ticketStatusRepository;
	private final UserService userService;
	private final TicketService ticketService;
	private DashboardService dashboardService;
	private final TicketCommentRepository ticketCommentRepository;

	// Constructor injection for all dependencies
	public TicketController(UserRepository userRepository, TicketRepository ticketRepository,
			TicketPriorityRepository ticketPriorityRepository, TicketTypeRepository ticketTypeRepository,
			TicketStatusRepository ticketStatusRepository, UserService userService, TicketService ticketService,
			DashboardService dashboardService, TicketCommentRepository ticketCommentRepository) {
		this.userRepository = userRepository;
		this.ticketRepository = ticketRepository;
		this.ticketPriorityRepository = ticketPriorityRepository;
		this.ticketTypeRepository = ticketTypeRepository;
		this.ticketStatusRepository = ticketStatusRepository;
		this.userService = userService;
		this.ticketService = ticketService;
		this.dashboardService = dashboardService;
		this.ticketCommentRepository = ticketCommentRepository;
	}

	@PostMapping("/user/tickets/create")
	public String createTicket(@RequestParam("title") String title, @RequestParam("description") String description,
			@RequestParam("priorityId") Integer priorityId,
			@RequestParam(value = "typeId", required = false) Integer typeId, Principal principal) {
		try {

			if (principal == null) {
				throw new RuntimeException("You must be logged in to create a ticket.");
			}

			Ticket ticket = new Ticket();
			ticket.setTitle(title);
			ticket.setDescription(description);
			ticket.setCreatedAt(LocalDateTime.now());

			Optional<User> userOptional = userRepository.findByUsername(principal.getName());
			User createdBy = userOptional
					.orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

			ticket.setCreatedBy(createdBy);

			TicketStatus defaultStatus = ticketStatusRepository.findById(1)
					.orElseThrow(() -> new RuntimeException("Status ID 1 (Open) not found"));
			ticket.setStatus(defaultStatus);

			TicketPriority priority = ticketPriorityRepository.findById(priorityId)
					.orElseThrow(() -> new RuntimeException("Priority not found: " + priorityId));
			ticket.setPriority(priority);

			if (typeId != null) {
				TicketType type = ticketTypeRepository.findById(typeId).orElse(null);
				ticket.setType(type);
			}

			ticketRepository.save(ticket);

			return "redirect:/user/tickets";

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@GetMapping("/tickets")
	public String getTickets(@RequestParam(name = "statusId", required = false) Integer statusId,
			@RequestParam(name = "priorityId", required = false) Integer priorityId,
			@RequestParam(name = "typeId", required = false) Integer typeId,
			@RequestParam(name = "search", required = false) String search, Model model, Principal principal,
			@RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

		// Récupérer l'utilisateur connecté
		String username = principal.getName();
		User user = dashboardService.getUserByUsername(username);
		Long userId = user.getId();

		// Récupérer les tickets filtrés
		List<Ticket> filteredTickets = ticketService.filterTickets(userId, statusId, priorityId, typeId, search);

		// Ajouter les données nécessaires au modèle
		model.addAttribute("tickets", filteredTickets);
		model.addAttribute("statuses", ticketStatusRepository.findAll());
		model.addAttribute("priorities", ticketPriorityRepository.findAll());
		model.addAttribute("types", ticketTypeRepository.findAll());

		// Pour garder les valeurs sélectionnées dans le formulaire
		model.addAttribute("selectedStatusId", statusId);
		model.addAttribute("selectedPriorityId", priorityId);
		model.addAttribute("selectedTypeId", typeId);
		model.addAttribute("search", search);

		// Sinon, page entière
		return "user/Tickets";
	}

	@PostMapping("/user/tickets/delete/{id}")
	public String deleteTicketUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		try {
			ticketRepository.deleteById(id);
			redirectAttributes.addFlashAttribute("successMessage", "Ticket deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete the ticket.");
		}
		return "redirect:/user/tickets";
	}

	@PostMapping("/user/tickets/update")
	public String updateTicket(@ModelAttribute Ticket ticket) {
		Ticket existing = ticketRepository.findById(ticket.getTicketId()).orElseThrow();
		existing.setTitle(ticket.getTitle());
		existing.setDescription(ticket.getDescription());
		existing.setPriority(ticket.getPriority());
		existing.setType(ticket.getType());
		ticketRepository.save(existing);
		return "redirect:/user/tickets";
	}

	@GetMapping("/user/tickets/details/{id}")
	@ResponseBody
	public ResponseEntity<?> getTicketDetails(@PathVariable("id") Long id) {
		Optional<Ticket> optionalTicket = ticketRepository.findById(id);
		if (optionalTicket.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Ticket ticket = optionalTicket.get();

		// Create a DTO to avoid returning full entities
		Map<String, Object> data = new HashMap<>();
		data.put("ticketId", ticket.getTicketId());
		data.put("title", ticket.getTitle());
		data.put("description", ticket.getDescription());
		data.put("createdAt", ticket.getCreatedAt().toString());
		data.put("updatedAt", ticket.getUpdatedAt().toString());
		data.put("status", ticket.getStatus().getStatusName());
		data.put("priority", ticket.getPriority().getPriorityName());
		data.put("type", ticket.getType().getTypeName());
		data.put("createdBy", ticket.getCreatedBy().getUsername());
		data.put("assignedTo", ticket.getAssignedTo() != null ? ticket.getAssignedTo().getUsername() : null);

		// Load comments (ordered by createdAt ascending)
		List<TicketComment> commentList = ticketCommentRepository
				.findByTicket_TicketIdOrderByCreatedAtAsc(ticket.getTicketId());

		List<Map<String, Object>> comments = new ArrayList<>();
		for (TicketComment c : commentList) {
			Map<String, Object> map = new HashMap<>();
			map.put("username", c.getUser().getUsername());
			map.put("comment", c.getComment());
			map.put("createdAt", c.getCreatedAt().toString());
			comments.add(map);
		}
		data.put("comments", comments);

		// Load history
		List<Map<String, Object>> history = ticket.getStatusHistory().stream().map(h -> {
			Map<String, Object> hEntry = new HashMap<>();
			hEntry.put("status", h.getStatus().getStatusName());
			hEntry.put("changedBy", h.getChangedBy().getUsername());
			hEntry.put("changedAt", h.getChangedAt().toString());
			return hEntry;
		}).collect(Collectors.toList());
		data.put("history", history);

		return ResponseEntity.ok(data);
	}

	@PostMapping("/user/tickets/{ticketId}/comments")
	@ResponseBody
	public ResponseEntity<?> addComment(@PathVariable("ticketId") Long ticketId,
			@RequestParam("commentText") String commentText, Principal principal) {

		User user = userRepository.findByUsername(principal.getName())
				.orElseThrow(() -> new RuntimeException("Authenticated user not found in database."));
		Ticket ticket = ticketRepository.findById(ticketId)
				.orElseThrow(() -> new RuntimeException("Ticket with ID " + ticketId + " not found."));

		TicketComment comment = new TicketComment();
		comment.setTicket(ticket);
		comment.setUser(user);
		comment.setComment(commentText);
		comment.setCreatedAt(LocalDateTime.now());

		ticketCommentRepository.save(comment);

		return ResponseEntity.ok("Comment added successfully");
	}

	@GetMapping("/user/tickets/{ticketId}/comments")
	@ResponseBody
	public ResponseEntity<?> getComments(@PathVariable("ticketId") Long ticketId) {
		List<TicketComment> comments = ticketCommentRepository.findByTicket_TicketIdOrderByCreatedAtAsc(ticketId);
		List<Map<String, Object>> result = new ArrayList<>();

		for (TicketComment c : comments) {
			Map<String, Object> map = new HashMap<>();
			map.put("username", c.getUser().getUsername());
			map.put("comment", c.getComment());
			map.put("createdAt", c.getCreatedAt().toString());
			result.add(map);
		}

		return ResponseEntity.ok(result);
	}

	// Partie tickets d Admiiin
	@GetMapping("/admin/tickets")
	public String filterTickets(@RequestParam(required = false) Integer statusId,
			@RequestParam(required = false) Integer priorityId, @RequestParam(required = false) Integer typeId,
			@RequestParam(required = false) Long assignedToId, @RequestParam(required = false) String keyword,
			@RequestParam(required = false) String dateRange, @RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate, Model model) {

		LocalDateTime startDateTime = null;
		LocalDateTime endDateTime = null;
		LocalDate today = LocalDate.now();

		if (dateRange != null) {
			switch (dateRange) {
			case "today":
				startDateTime = today.atStartOfDay();
				endDateTime = today.atTime(23, 59, 59);
				break;
			case "week":
				startDateTime = today.with(java.time.DayOfWeek.MONDAY).atStartOfDay();
				endDateTime = today.with(java.time.DayOfWeek.SUNDAY).atTime(23, 59, 59);
				break;
			case "month":
				startDateTime = today.withDayOfMonth(1).atStartOfDay();
				endDateTime = today.withDayOfMonth(today.lengthOfMonth()).atTime(23, 59, 59);
				break;
			case "custom":
				try {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					if (startDate != null && !startDate.isEmpty()) {
						startDateTime = LocalDate.parse(startDate, formatter).atStartOfDay();
					}
					if (endDate != null && !endDate.isEmpty()) {
						endDateTime = LocalDate.parse(endDate, formatter).atTime(23, 59, 59);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}

		List<Ticket> tickets = ticketRepository.searchTicketsWithFilters(statusId, priorityId, typeId, assignedToId,
				keyword, startDateTime, endDateTime);

		model.addAttribute("tickets", tickets);
		model.addAttribute("users", userRepository.findByRole_RoleName("User"));
		model.addAttribute("technicians", userRepository.findByRole_RoleName("Technicien"));
		model.addAttribute("priorities", ticketPriorityRepository.findAll());
		model.addAttribute("types", ticketTypeRepository.findAll());
		model.addAttribute("statuses", ticketStatusRepository.findAll());

		// Ajout des valeurs sélectionnées
		model.addAttribute("selectedStatusId", statusId);
		model.addAttribute("selectedPriorityId", priorityId);
		model.addAttribute("selectedTypeId", typeId);
		model.addAttribute("selectedAssignedToId", assignedToId);
		model.addAttribute("selectedKeyword", keyword);
		model.addAttribute("selectedDateRange", dateRange);
		model.addAttribute("selectedStartDate", startDate);
		model.addAttribute("selectedEndDate", endDate);

		return "admin/Tickets";
	}

	@PostMapping("/admin/tickets/create")
	public String createTicketByAdmin(@RequestParam String title, @RequestParam String description,
			@RequestParam Integer priorityId, @RequestParam Integer typeId, @RequestParam Long createdById,
			@RequestParam(required = false) Long assignedToUserId) {
		Ticket ticket = new Ticket();
		ticket.setTitle(title);
		ticket.setDescription(description);
		ticket.setCreatedAt(LocalDateTime.now());

		User createdBy = userRepository.findById(createdById).orElseThrow();
		ticket.setCreatedBy(createdBy);

		if (assignedToUserId != null) {
			ticket.setAssignedTo(userRepository.findById(assignedToUserId).orElse(null));
		}

		ticket.setPriority(ticketPriorityRepository.findById(priorityId).orElseThrow());
		ticket.setType(ticketTypeRepository.findById(typeId).orElseThrow());
		ticket.setStatus(ticketStatusRepository.findById(1).orElseThrow()); // status: Open

		ticketRepository.save(ticket);
		return "redirect:/admin/tickets";
	}

	@GetMapping("/tickets/delete/{id}")
	public String deleteTicket(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		try {
			ticketService.deleteTicketById(id);
			redirectAttributes.addFlashAttribute("successMessage", "Ticket deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error while deleting ticket.");
		}
		return "redirect:/admin/tickets";
	}

	// update ticket
	@GetMapping("/tickets/edit/{id}")
	public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
		Optional<Ticket> ticketOpt = ticketRepository.findById(id);
		if (ticketOpt.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Ticket not found");
			return "redirect:/admin/tickets";
		}
		Ticket ticket = ticketOpt.get();

		// This is the ticket to be edited, we name it "ticketToEdit" to avoid conflicts
		model.addAttribute("ticketToEdit", ticket);

		// We still need all the other data for the main page list and dropdowns
		model.addAttribute("tickets", ticketRepository.findAll()); // Or your filtered list
		model.addAttribute("statuses", ticketStatusRepository.findAll());
		model.addAttribute("priorities", ticketPriorityRepository.findAll());
		model.addAttribute("types", ticketTypeRepository.findAll());
		model.addAttribute("users", userRepository.findAll());
		model.addAttribute("technicians", userRepository.findByRole_RoleName("Technicien"));

		// Return the main view. JavaScript will open the edit modal.
		return "admin/Tickets";
	}

	@PostMapping("/tickets/edit")
	public String updateTicket(@ModelAttribute("ticketToEdit") Ticket updatedTicket,
			RedirectAttributes redirectAttributes) {
		try {
			// The updatedTicket object is bound from the form.
			// We just need to ensure the full related objects are set before saving.
			Optional<Ticket> existingTicketOpt = ticketRepository.findById(updatedTicket.getTicketId());
			if (existingTicketOpt.isEmpty()) {
				redirectAttributes.addFlashAttribute("errorMessage", "Ticket not found.");
				return "redirect:/admin/tickets";
			}

			Ticket ticketToSave = existingTicketOpt.get();
			TicketStatus newStatus = ticketStatusRepository.findById(updatedTicket.getStatus().getStatusId())
					.orElse(null);

			// On vérifie si le nouveau statut est "Resolved" et si le ticket n'a pas déjà
			// une date de résolution
			if (newStatus != null && "Resolved".equalsIgnoreCase(newStatus.getStatusName())
					&& ticketToSave.getResolvedAt() == null) {
				ticketToSave.setResolvedAt(LocalDateTime.now());
			}

			// Update fields from the form
			ticketToSave.setTitle(updatedTicket.getTitle());
			ticketToSave.setDescription(updatedTicket.getDescription());
			ticketToSave
					.setStatus(ticketStatusRepository.findById(updatedTicket.getStatus().getStatusId()).orElse(null));
			ticketToSave.setPriority(
					ticketPriorityRepository.findById(updatedTicket.getPriority().getPriorityId()).orElse(null));
			ticketToSave.setType(ticketTypeRepository.findById(updatedTicket.getType().getTypeId()).orElse(null));
			ticketToSave.setStatus(newStatus);

			if (updatedTicket.getAssignedTo() != null && updatedTicket.getAssignedTo().getId() != null) {
				ticketToSave.setAssignedTo(userRepository.findById(updatedTicket.getAssignedTo().getId()).orElse(null));
			} else {
				ticketToSave.setAssignedTo(null); // Handle un-assigning
			}

			ticketService.updateTicket(ticketToSave); // Use the service to save
			redirectAttributes.addFlashAttribute("successMessage", "Ticket updated successfully.");

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage", "Error updating ticket.");
		}

		return "redirect:/admin/tickets";
	}

	@PostMapping("/tickets/assign-form")
	public String assignTicket(
			// @RequestParam lie un paramètre de la requête (du formulaire) à un argument de
			// la méthode.
			// "ticketId" doit correspondre à l'attribut 'name' dans votre balise <input>.
			@RequestParam("ticketId") Long ticketId,

			@RequestParam("technicianId") Long technicianId,

			// 'required = false' signifie que ce champ peut être vide ou absent.
			@RequestParam(value = "assignmentNote", required = false) String assignmentNote,

			// Pour une checkbox, si elle n'est pas cochée, aucun paramètre n'est envoyé.
			// 'defaultValue = "false"' garantit que la variable 'notifyTechnician' sera
			// 'false' dans ce cas.
			// Si elle est cochée, elle enverra sa 'value' ("true"), que Spring convertira
			// correctement.
			@RequestParam(value = "notifyTechnician", defaultValue = "false") boolean notifyTechnician,

			// RedirectAttributes est utilisé pour passer des messages à la page suivante
			// après une redirection.
			RedirectAttributes redirectAttributes) {
		// Étape 1 : Créer l'objet DTO (l'"enveloppe") pour passer les données
		// proprement au service.
		AssignTicketRequest request = new AssignTicketRequest();
		request.setTicketId(ticketId);
		request.setTechnicianId(technicianId);
		request.setAssignmentNote(assignmentNote);
		request.setNotifyTechnician(notifyTechnician);

		// Étape 2 : Appeler le service dans un bloc try-catch pour gérer les erreurs
		// éventuelles.
		try {
			// On passe l'objet request complet à la couche de service.
			ticketService.assignTicket(request);
			// Si tout s'est bien passé, on prépare un message de succès.
			redirectAttributes.addFlashAttribute("successMessage",
					"Ticket #" + ticketId + " a été assigné avec succès !");
		} catch (Exception e) {
			// Si une erreur survient (ex: ticket non trouvé), on prépare un message
			// d'erreur.
			e.printStackTrace(); // Affiche l'erreur complète dans la console du serveur pour le débogage.
			redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'assignation : " + e.getMessage());
		}

		// Étape 3 : Rediriger l'utilisateur vers la page principale des tickets.
		return "redirect:/admin/tickets";
	}

}
