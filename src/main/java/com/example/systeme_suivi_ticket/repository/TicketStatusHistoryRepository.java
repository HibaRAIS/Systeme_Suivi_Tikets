package com.example.systeme_suivi_ticket.repository;

import com.example.systeme_suivi_ticket.model.TicketStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketStatusHistoryRepository extends JpaRepository<TicketStatusHistory, Long> {
    List<TicketStatusHistory> findByTicket_TicketIdOrderByChangedAtAsc(Long ticketId);
}
