package com.example.systeme_suivi_ticket.repository;

import com.example.systeme_suivi_ticket.model.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {
    List<TicketComment> findByTicket_TicketIdOrderByCreatedAtAsc(Long ticketId);
}
