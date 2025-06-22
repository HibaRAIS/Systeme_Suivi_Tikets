package com.example.systeme_suivi_ticket.dto;

public class AssignTicketRequest {
    private Long ticketId;
    private Long technicianId;
    private String assignmentNote;
    private boolean notifyTechnician;

    // Générez les Getters et Setters pour tous les champs
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public Long getTechnicianId() { return technicianId; }
    public void setTechnicianId(Long technicianId) { this.technicianId = technicianId; }

    public String getAssignmentNote() { return assignmentNote; }
    public void setAssignmentNote(String assignmentNote) { this.assignmentNote = assignmentNote; }

    public boolean isNotifyTechnician() { return notifyTechnician; }
    public void setNotifyTechnician(boolean notifyTechnician) { this.notifyTechnician = notifyTechnician; }

    @Override
    public String toString() {
        return "AssignTicketRequest{" +
                "ticketId=" + ticketId +
                ", technicianId=" + technicianId +
                ", assignmentNote='" + assignmentNote + '\'' +
                ", notifyTechnician=" + notifyTechnician +
                '}';
    }

}