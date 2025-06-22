package com.example.systeme_suivi_ticket.repository;

import com.example.systeme_suivi_ticket.model.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTypeRepository extends JpaRepository<TicketType, Integer> {
}
