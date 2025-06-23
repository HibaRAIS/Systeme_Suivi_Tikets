package com.example.systeme_suivi_ticket.dto;

public class UserSummaryDTO {
    private String fullName;
    private String initials;

    // Constructeurs, Getters et Setters
    public UserSummaryDTO(String fullName, String initials) {
        this.fullName = fullName;
        this.initials = initials;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getInitials() { return initials; }
    public void setInitials(String initials) { this.initials = initials; }
}
