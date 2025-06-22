package com.example.systeme_suivi_ticket.repository;

import com.example.systeme_suivi_ticket.model.Ticket;
import com.example.systeme_suivi_ticket.model.TicketStatus;
import com.example.systeme_suivi_ticket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    long countByStatus(TicketStatus status);
    List<Ticket> findTop5ByCreatedByOrderByCreatedAtDesc(User user);
    List<Ticket> findByAssignedToOrderByCreatedAtDesc(User technician);
    List<Ticket> findTop5ByOrderByCreatedAtDesc();
    // Fetch all tickets created by the given user
    List<Ticket> findByCreatedBy(User createdBy);
    long countByCreatedById(Long userId);
    List<Ticket> findTop5ByCreatedByIdOrderByCreatedAtDesc(Long userId);
    List<Ticket> findAllByCreatedById(Long userId);
    long countByAssignedToIsNull();

    @Query("SELECT t FROM Ticket t WHERE " + "t.createdBy.id = :userId AND "
            + "(:statusId IS NULL OR t.status.statusId = :statusId) AND "
            + "(:priorityId IS NULL OR t.priority.priorityId = :priorityId) AND "
            + "(:typeId IS NULL OR t.type.typeId = :typeId) AND "
            + "(:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Ticket> findTicketsWithFilters(@Param("userId") Long userId, @Param("statusId") Integer statusId,
                                        @Param("priorityId") Integer priorityId, @Param("typeId") Integer typeId, @Param("search") String search);

    @Query("SELECT t FROM Ticket t WHERE " +
            "(:statusId IS NULL OR t.status.statusId = :statusId) AND " +
            "(:priorityId IS NULL OR t.priority.priorityId = :priorityId) AND " +
            "(:typeId IS NULL OR t.type.typeId = :typeId) AND " +
            "(:assignedToId IS NULL OR (:assignedToId = 0 AND t.assignedTo IS NULL) OR (t.assignedTo.id = :assignedToId)) AND " +
            "(:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:startDateTime IS NULL OR t.createdAt >= :startDateTime) AND " +
            "(:endDateTime IS NULL OR t.createdAt <= :endDateTime) ")
    List<Ticket> searchTicketsWithFilters(
            @Param("statusId") Integer statusId,
            @Param("priorityId") Integer priorityId,
            @Param("typeId") Integer typeId,
            @Param("assignedToId") Long assignedToId,
            @Param("keyword") String keyword,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);
    //charts of admin dashboard
    @Query("SELECT t.status.statusName, COUNT(t.ticketId) FROM Ticket t GROUP BY t.status.statusName")
    List<Object[]> countTicketsByStatus();
    // chrts deuxieme grahe
    @Query("SELECT YEAR(t.createdAt), MONTH(t.createdAt), COUNT(t.ticketId) " +
            "FROM Ticket t " +
            "WHERE t.createdAt >= :startDate " +
            "GROUP BY YEAR(t.createdAt), MONTH(t.createdAt) " +
            "ORDER BY YEAR(t.createdAt), MONTH(t.createdAt)")
    List<Object[]> findMonthlyTicketCreationCounts(@Param("startDate") LocalDateTime startDate);
    // === le date de resolution ===
    @Query("SELECT YEAR(t.resolvedAt), MONTH(t.resolvedAt), COUNT(t.ticketId) " +
            "FROM Ticket t " +
            "WHERE t.resolvedAt >= :startDate " +
            "GROUP BY YEAR(t.resolvedAt), MONTH(t.resolvedAt) " +
            "ORDER BY YEAR(t.resolvedAt), MONTH(t.resolvedAt)")
    List<Object[]> findMonthlyTicketResolutionCounts(@Param("startDate") LocalDateTime startDate);
    //week et year
    @Query("SELECT DATE(t.createdAt), COUNT(t.ticketId) FROM Ticket t WHERE t.createdAt >= :startDate GROUP BY DATE(t.createdAt)")
    List<Object[]> findDailyTicketCreationCounts(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT YEAR(t.createdAt), COUNT(t.ticketId) FROM Ticket t WHERE t.createdAt >= :startDate GROUP BY YEAR(t.createdAt)")
    List<Object[]> findYearlyTicketCreationCounts(@Param("startDate") LocalDateTime startDate);
}

