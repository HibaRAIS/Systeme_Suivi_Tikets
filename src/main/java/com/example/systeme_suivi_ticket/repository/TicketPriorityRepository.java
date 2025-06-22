package com.example.systeme_suivi_ticket.repository;

import com.example.systeme_suivi_ticket.model.TicketPriority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketPriorityRepository extends JpaRepository<TicketPriority, Integer> {
}