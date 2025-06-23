package com.example.systeme_suivi_ticket.service;

import com.example.systeme_suivi_ticket.dto.AssignTicketRequest;
import com.example.systeme_suivi_ticket.dto.ChartDataDTO;
import com.example.systeme_suivi_ticket.dto.TechnicianDashboardStatsDTO;
import com.example.systeme_suivi_ticket.dto.TicketDetailDTO;
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
    //technicien
    TechnicianDashboardStatsDTO getTechnicianDashboardStats(User technician);
    // RECHERCHE DANS TECHNICIAN
    List<Ticket> findTicketsAssignedToWithSearch(User technician, String keyword);
    // NOUVELLE méthode pour le tri
    List<Ticket> findSortedTicketsAssignedTo(User technician, String sortField, String sortDir);
    // NOUVELLE méthode pour filtrer
    List<Ticket> findAndFilterTickets(User technician, String statusFilter, String keyword, String sortField, String sortDir);
    //details tickets
    TicketDetailDTO getTicketDetailsById(Long ticketId);
    //details tickets update
    void updateTicketStatusAndPriority(Long ticketId, Integer statusId, Integer priorityId, User changedBy);
    //Ajouter commentaire
    void addCommentToTicket(Long ticketId, String commentText, User author);

}

