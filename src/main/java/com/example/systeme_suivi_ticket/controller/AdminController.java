package com.example.systeme_suivi_ticket.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.systeme_suivi_ticket.dto.ChartDataDTO;
import com.example.systeme_suivi_ticket.model.User;
import com.example.systeme_suivi_ticket.repository.RoleRepository;
import com.example.systeme_suivi_ticket.repository.TicketPriorityRepository;
import com.example.systeme_suivi_ticket.repository.TicketRepository;
import com.example.systeme_suivi_ticket.repository.TicketStatusRepository;
import com.example.systeme_suivi_ticket.repository.TicketTypeRepository;
import com.example.systeme_suivi_ticket.repository.UserRepository;
import com.example.systeme_suivi_ticket.service.TicketService;
import com.example.systeme_suivi_ticket.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Autowired
	private TicketService ticketService;

	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private TicketStatusRepository ticketStatusRepository;
	@Autowired
	private TicketPriorityRepository ticketPriorityRepository;
	@Autowired
	private TicketTypeRepository ticketTypeRepository;

	@GetMapping("/dashboard")
	public String dashboard(Model model) {

		// Récupérer les statistiques depuis les services
		long totalUsers = userService.countTotalUsers();
		long totalTickets = ticketService.countAllTickets();
		long openTickets = ticketService.countOpenTickets();

		// Ajouter chaque statistique au modèle pour la rendre disponible dans Thymeleaf
		model.addAttribute("totalUsers", totalUsers);
		model.addAttribute("totalTickets", totalTickets);
		model.addAttribute("openTickets", openTickets);

		// Pour "Average Response", nous mettons une valeur statique pour l'instant.
		model.addAttribute("unassignedTickets", ticketService.countUnassignedTickets());

		model.addAttribute("techniciansCount", userService.countUsersByRoleName("TECHNICIAN"));
		model.addAttribute("resolvedTicketsCount", ticketService.countResolvedTickets());

		// === LE GRAPHIQUE DE DISTRIBUTION ===
		Map<String, Long> statusDistribution = ticketService.getTicketStatusDistribution();

		// On sépare les clés (labels) et les valeurs (données) en deux listes
		// distinctes
		model.addAttribute("statusLabels", new ArrayList<>(statusDistribution.keySet()));
		model.addAttribute("statusData", new ArrayList<>(statusDistribution.values()));

		// === AJOUTER LA LOGIQUE POUR LE GRAPHIQUE EN LIGNES ===
		ChartDataDTO newTicketsData = ticketService.getMonthlyTicketCreationStats();
		model.addAttribute("overviewLabels", newTicketsData.getLabels());
		model.addAttribute("overviewNewTicketsData", newTicketsData.getData());

		// Pour la 2ème ligne ("Resolved")
		ChartDataDTO resolvedTicketsData = ticketService.getMonthlyTicketResolutionStats();
		model.addAttribute("overviewResolvedTicketsData", resolvedTicketsData.getData());

		// === AJOUTER LES DONNÉES POUR LES LISTES D'ACTIVITÉ RÉCENTE ===
		model.addAttribute("recentTickets", ticketService.findRecentTickets());
		model.addAttribute("recentUsers", userService.findRecentUsers());

		// Retourner le nom de la vue
		return "admin/Dashboard";
	}

	@GetMapping("/users")
	public String listUsers(@RequestParam(name = "userType", required = false) Integer userType,
			@RequestParam(name = "userStatus", required = false) Boolean userStatus,
			@RequestParam(name = "searchTerm", required = false) String searchTerm, Model model) {

		// Récupérer les utilisateurs filtrés avec un service
		List<User> filteredUsers = userService.findUsersByFilters(userType, userStatus, searchTerm);

		model.addAttribute("users", filteredUsers);
		model.addAttribute("roles", roleRepository.findAll());
		model.addAttribute("user", new User());
		model.addAttribute("totalUsers", userService.countTotalUsers());
		model.addAttribute("technicians", userService.countUsersByRoleName("TECHNICIAN"));
		model.addAttribute("activeUsers", userService.countActiveUsers());
		model.addAttribute("inactiveUsers", userService.countInactiveUsers());

		// Passer aussi les valeurs pour que le formulaire garde le filtre sélectionné
		model.addAttribute("selectedUserType", userType);
		model.addAttribute("selectedUserStatus", userStatus);
		model.addAttribute("searchTerm", searchTerm);

		return "admin/Users";
	}

	@GetMapping("users/add")
	public String showAddForm(Model model) {
		model.addAttribute("user", new User());
		return "admin/AddUser";
	}

	@PostMapping("users/add")
	public String addUser(@ModelAttribute User user) {
		userService.saveUser(user);
		return "redirect:/admin/users";
	}

	@GetMapping("users/edit/{id}")
	public String showEditForm(@PathVariable Long id, Model model) {
		userService.getUserById(id).ifPresent(user -> model.addAttribute("user", user));
		model.addAttribute("roles", roleRepository.findAll());
		return "admin/EditUser";
	}

	@PostMapping("/users/update")
	public String updateUser(@ModelAttribute("user") User user) {
		userService.updateUser(user);
		return "redirect:/admin/users";
	}

	@GetMapping("users/delete/{id}")
	public String deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return "redirect:/admin/users";
	}

	@PostMapping("/users/send-email")
	public String sendEmail(@RequestParam("to") String to, @RequestParam("subject") String subject,
			@RequestParam("body") String body, RedirectAttributes redirectAttributes) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(fromEmail);
			message.setTo(to);
			message.setSubject(subject);
			message.setText(body);
			mailSender.send(message);
			redirectAttributes.addFlashAttribute("successMessage", "Email envoyé avec succès !");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'envoi de l'email.");
			e.printStackTrace();
		}
		return "redirect:/admin/users";
	}

}
