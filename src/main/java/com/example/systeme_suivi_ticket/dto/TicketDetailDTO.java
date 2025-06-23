package com.example.systeme_suivi_ticket.dto;


import java.util.List;

public class TicketDetailDTO {
    private Long ticketId;
    private String title;
    private String description;
    private String status;
    private String statusClass; // Pour la couleur du badge (ex: badge-open)
    private String priority;
    private String priorityClass; // Pour la couleur du badge (ex: badge-high)
    private String type;
    private String createdAt;
    private String updatedAt;
    private UserSummaryDTO createdBy;
    private UserSummaryDTO assignedTo;
    private List<CommentDTO> comments;
    private List<HistoryEntryDTO> history;
    private Integer statusId;
    private Integer priorityId;



    // Getters et Setters pour tous les champs...
    // (Générez-les avec votre IDE)
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusClass() { return statusClass; }
    public void setStatusClass(String statusClass) { this.statusClass = statusClass; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getPriorityClass() { return priorityClass; }
    public void setPriorityClass(String priorityClass) { this.priorityClass = priorityClass; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public UserSummaryDTO getCreatedBy() { return createdBy; }
    public void setCreatedBy(UserSummaryDTO createdBy) { this.createdBy = createdBy; }
    public UserSummaryDTO getAssignedTo() { return assignedTo; }
    public void setAssignedTo(UserSummaryDTO assignedTo) { this.assignedTo = assignedTo; }
    public List<CommentDTO> getComments() { return comments; }
    public void setComments(List<CommentDTO> comments) { this.comments = comments; }
    public List<HistoryEntryDTO> getHistory() { return history; }
    public void setHistory(List<HistoryEntryDTO> history) { this.history = history; }
    public Integer getStatusId() { return statusId; }
    public void setStatusId(Integer statusId) { this.statusId = statusId; }
    public Integer getPriorityId() { return priorityId; }
    public void setPriorityId(Integer priorityId) { this.priorityId = priorityId; }
}