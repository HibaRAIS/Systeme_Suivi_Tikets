package com.example.systeme_suivi_ticket.dto;


public class TechnicianDashboardStatsDTO {

    private String technicianName;
    private long unresolvedCount;
    private long totalTickets;
    private long resolvedCount;
    private long inProgressCount;
    private long highPriorityCount;

    // Getters and Setters
    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public long getUnresolvedCount() {
        return unresolvedCount;
    }

    public void setUnresolvedCount(long unresolvedCount) {
        this.unresolvedCount = unresolvedCount;
    }

    public long getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(long totalTickets) {
        this.totalTickets = totalTickets;
    }

    public long getResolvedCount() {
        return resolvedCount;
    }

    public void setResolvedCount(long resolvedCount) {
        this.resolvedCount = resolvedCount;
    }

    public long getInProgressCount() {
        return inProgressCount;
    }

    public void setInProgressCount(long inProgressCount) {
        this.inProgressCount = inProgressCount;
    }

    public long getHighPriorityCount() {
        return highPriorityCount;
    }

    public void setHighPriorityCount(long highPriorityCount) {
        this.highPriorityCount = highPriorityCount;
    }
}
