package com.example.systeme_suivi_ticket.controller;

import com.example.systeme_suivi_ticket.dto.TicketDetailDTO;
import com.example.systeme_suivi_ticket.model.User;
import com.example.systeme_suivi_ticket.service.TicketService;
import com.example.systeme_suivi_ticket.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketApiController {

    @Autowired
    private TicketService ticketService;

    // AJOUT : Injection du service utilisateur pour récupérer l'utilisateur connecté
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketDetails(@PathVariable Long id) {
        try {
            TicketDetailDTO ticketDetails = ticketService.getTicketDetailsById(id);
            return ResponseEntity.ok(ticketDetails);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Log l'erreur pour le débogage
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching ticket details.");
        }
    }

    // Classe interne pour la requête de mise à jour (inchangée)
    public static class UpdateTicketRequest {
        private Integer statusId;
        private Integer priorityId;

        // Getters et Setters
        public Integer getStatusId() { return statusId; }
        public void setStatusId(Integer statusId) { this.statusId = statusId; }
        public Integer getPriorityId() { return priorityId; }
        public void setPriorityId(Integer priorityId) { this.priorityId = priorityId; }
    }


    // Classe interne pour la requête d'ajout de commentaire
    public static class AddCommentRequest {
        private String commentText;

        // Getter et Setter
        public String getCommentText() { return commentText; }
        public void setCommentText(String commentText) { this.commentText = commentText; }
    }

    // ===================================================================
    // NOUVEL ENDPOINT POUR LA MISE À JOUR DU TICKET
    // ===================================================================
    @PostMapping("/{id}/update")
    public ResponseEntity<?> updateTicket(@PathVariable Long id,
                                          @RequestBody UpdateTicketRequest request,
                                          @AuthenticationPrincipal UserDetails userDetails) {

        // 1. Vérifier que l'utilisateur est authentifié
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }

        try {
            // 2. Récupérer l'entité User complète à partir de son nom d'utilisateur
            User currentUser = userService.findByUsername(userDetails.getUsername());

            // 3. Appeler le service pour effectuer la mise à jour
            ticketService.updateTicketStatusAndPriority(id, request.getStatusId(), request.getPriorityId(), currentUser);

            // 4. Renvoyer une réponse de succès
            return ResponseEntity.ok().body("Ticket updated successfully.");

        } catch (EntityNotFoundException e) {
            // Gérer le cas où le ticket, le statut ou la priorité n'est pas trouvé
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Gérer les autres erreurs inattendues
            e.printStackTrace(); // Important pour le débogage
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred during the update.");
        }
    }

    // ===================================================================
    // NOUVEL ENDPOINT POUR AJOUTER UN COMMENTAIRE
    // ===================================================================
    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long id,
                                        @RequestBody AddCommentRequest request,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }

        try {
            User currentUser = userService.findByUsername(userDetails.getUsername());
            ticketService.addCommentToTicket(id, request.getCommentText(), currentUser);

            // Pour une meilleure UX, renvoyons les détails mis à jour du ticket
            TicketDetailDTO updatedTicketDetails = ticketService.getTicketDetailsById(id);
            return ResponseEntity.ok(updatedTicketDetails);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred while adding the comment.");
        }
    }
}
