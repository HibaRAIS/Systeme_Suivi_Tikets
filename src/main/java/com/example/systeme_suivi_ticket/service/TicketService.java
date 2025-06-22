package com.example.systeme_suivi_ticket.service;

import com.example.systeme_suivi_ticket.dto.AssignTicketRequest;
import com.example.systeme_suivi_ticket.dto.ChartDataDTO;
import com.example.systeme_suivi_ticket.model.Ticket;
import com.example.systeme_suivi_ticket.model.TicketStatus;
import com.example.systeme_suivi_ticket.model.User;

import java.util.List;
import java.util.Map;

public interface TicketService {
    long countAllTickets();
    long countByStatus(TicketStatus status);
    List<Ticket> findRecentTicketsByUser(User user);
    List<Ticket> findTicketsAssignedTo(User technician);
    List<TicketStatus> getAllStatuses();
    // List<TicketPriority> getAllPriorities();
    // List<TicketType> getAllTypes(); // If TicketType exists
    void createTicket(String title, String description, Integer priorityId, Integer typeId, User createdBy);
    void deleteTicketById(Long id);
    void updateTicket(Ticket ticket);
    void assignTicket(AssignTicketRequest request);
    long countOpenTickets();
    long countResolvedTickets();
    long countUnassignedTickets();
    Map<String, Long> getTicketStatusDistribution();
    List<Ticket> findRecentTickets();
    ChartDataDTO getMonthlyTicketCreationStats();
    ChartDataDTO getMonthlyTicketResolutionStats();
    ChartDataDTO getTicketStatsByPeriod(String period, String type); // type peut être "created" ou "resolved"
    public List<Ticket> filterTickets(Long userId, Integer statusId, Integer priorityId, Integer typeId, String search);

}

