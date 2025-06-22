package com.example.systeme_suivi_ticket.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TicketID")
    private Long ticketId;

    @Column(name = "Title")
    private String title;

    @Column(name = "Description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "StatusID")
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "PriorityID")
    private TicketPriority priority;

    @ManyToOne
    @JoinColumn(name = "TypeID")
    private TicketType type;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "ClosedAt")
    private LocalDateTime closedAt;

    @ManyToOne
    @JoinColumn(name = "CreatedByUserID")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "AssignedToUserID")
    private User assignedTo;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "AssignmentNote", columnDefinition = "TEXT")
    private String assignmentNote;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketComment> comments;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketStatusHistory> statusHistory;

    @Column(name = "UpdatedAt", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long id) {
        this.ticketId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public String getAssignmentNote() {
        return assignmentNote;
    }

    public void setAssignmentNote(String assignmentNote) {
        this.assignmentNote = assignmentNote;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public List<TicketComment> getComments() {
        return comments;
    }

    public void setComments(List<TicketComment> comments) {
        this.comments = comments;
    }

    public List<TicketStatusHistory> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<TicketStatusHistory> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}

