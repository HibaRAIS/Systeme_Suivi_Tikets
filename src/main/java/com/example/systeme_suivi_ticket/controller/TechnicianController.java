package com.example.systeme_suivi_ticket.controller;

import com.example.systeme_suivi_ticket.dto.TechnicianDashboardStatsDTO;
import com.example.systeme_suivi_ticket.model.Ticket;
import com.example.systeme_suivi_ticket.model.User;
import com.example.systeme_suivi_ticket.service.TicketService;
import com.example.systeme_suivi_ticket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class TechnicianController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    @GetMapping("/technician/dashboard")
    public String showTechnicianDashboard(Model model,
                                          @AuthenticationPrincipal UserDetails userDetails,
                                          @RequestParam(name = "keyword", required = false) String keyword) { // <-- Accepter le paramètre
        if (userDetails == null) {
            return "redirect:/login";
        }
        User technician = userService.findByUsername(userDetails.getUsername());

        // 1. Récupérer les statistiques
        TechnicianDashboardStatsDTO stats = ticketService.getTechnicianDashboardStats(technician);
        model.addAttribute("stats", stats);

        // 2. Récupérer la liste des tickets ( AVEC RECHERCHE)
        List<Ticket> assignedTickets = ticketService.findTicketsAssignedToWithSearch(technician, keyword);
        model.addAttribute("assignedTickets", assignedTickets);

        // 3. Renvoyer le mot-clé à la vue pour l'afficher dans le champ de recherche
        model.addAttribute("keyword", keyword);

        return "technicien/Dashboard";
    }


    @GetMapping("/technician/my-tickets")
    public String showMyTickets(Model model,
                                @AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam(defaultValue = "all") String statusFilter, // <-- Filtre de statut
                                @RequestParam(required = false) String keyword,         // <-- Mot-clé de recherche
                                @RequestParam(defaultValue = "updatedAt") String sortField,
                                @RequestParam(defaultValue = "desc") String sortDir) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        User technician = userService.findByUsername(userDetails.getUsername());

        // Utilise le nouveau service de filtre et de recherche
        List<Ticket> tickets = ticketService.findAndFilterTickets(technician, statusFilter, keyword, sortField, sortDir);

        model.addAttribute("tickets", tickets);

        // Envoyer les infos de filtre et de recherche pour l'état de l'UI
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("keyword", keyword);


        // ... reste des attributs pour le tri et le nom d'utilisateur ...
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("username", technician.getFirstName() != null ? technician.getFirstName() : technician.getUsername());

        return "technicien/my-tickets";
    }
}