package com.example.systeme_suivi_ticket.repository;

import com.example.systeme_suivi_ticket.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketStatusRepository extends JpaRepository<TicketStatus, Integer> {
    // @Query("SELECT s.statusName, COUNT(t.ticketId) FROM Ticket t JOIN t.status s
    // GROUP BY s.statusName")
    @Query("""
			    SELECT s.statusName, COUNT(t.ticketId)
			    FROM TicketStatus s
			    LEFT JOIN Ticket t ON t.status = s AND t.createdBy.username = :username
			    GROUP BY s.statusName
			""")
    List<Object[]> getStatusStatistics(@Param("username") String username);

}
