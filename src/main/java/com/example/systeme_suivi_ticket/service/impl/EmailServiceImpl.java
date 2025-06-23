package com.example.systeme_suivi_ticket.service.impl;

import com.example.systeme_suivi_ticket.model.Ticket;
import com.example.systeme_suivi_ticket.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendAssignmentNotification(Ticket ticket) {
        if (ticket.getAssignedTo() == null || ticket.getAssignedTo().getEmail() == null) {
            return; // Ne rien faire si le technicien ou son email n'existe pas
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@ticketsystem.com");
        message.setTo(ticket.getAssignedTo().getEmail());
        message.setSubject("Nouveau Ticket Assigné : #" + ticket.getTicketId());

        String text = String.format(
                "Bonjour %s,\n\nLe ticket #%d '%s' vous a été assigné.\n\nNote de l'administrateur:\n%s\n\nMerci.",
                ticket.getAssignedTo().getFirstName(),
                ticket.getTicketId(),
                ticket.getTitle(),
                ticket.getAssignmentNote() != null ? ticket.getAssignmentNote() : "Aucune note."
        );
        message.setText(text);

        mailSender.send(message);
    }

    // NOUVELLE MÉTHODE POUR LA CONFIRMATION DE CRÉATION
    @Override
    public void sendTicketCreationConfirmation(Ticket ticket) {
        // On vérifie que le créateur du ticket et son email existent.
        // Je suppose ici que votre modèle Ticket a une relation vers l'utilisateur qui l'a créé,
        // par exemple : private User createdBy;
        if (ticket.getCreatedBy() == null || ticket.getCreatedBy().getEmail() == null) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@ticketsystem.com");
        // On envoie l'email au créateur du ticket
        message.setTo(ticket.getCreatedBy().getEmail());
        message.setSubject("Confirmation de création de votre ticket : #" + ticket.getTicketId());

        String text = String.format(
                "Bonjour %s,\n\nVotre ticket #%d concernant '%s' a bien été créé.\n\nDescription que vous avez fournie:\n%s\n\nUn technicien prendra en charge votre demande dès que possible. Vous pouvez suivre son statut depuis votre espace personnel.\n\nMerci.",
                ticket.getCreatedBy().getFirstName(), // ou getUsername()
                ticket.getTicketId(),
                ticket.getTitle(),
                ticket.getDescription()
        );
        message.setText(text);

        mailSender.send(message);
    }



}
