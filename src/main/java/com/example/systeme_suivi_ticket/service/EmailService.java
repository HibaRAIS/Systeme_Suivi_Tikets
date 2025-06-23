package com.example.systeme_suivi_ticket.service;

import com.example.systeme_suivi_ticket.model.Ticket;

public interface EmailService {
    void sendAssignmentNotification(Ticket ticket);
    void sendTicketCreationConfirmation(Ticket ticket);
}