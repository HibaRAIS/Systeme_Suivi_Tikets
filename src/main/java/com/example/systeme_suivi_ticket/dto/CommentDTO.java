package com.example.systeme_suivi_ticket.dto;

public class CommentDTO {
    private String authorName;
    private String authorInitials;
    private String commentText;
    private String createdAt;

    // Constructeurs, Getters et Setters
    public CommentDTO(String authorName, String authorInitials, String commentText, String createdAt) {
        this.authorName = authorName;
        this.authorInitials = authorInitials;
        this.commentText = commentText;
        this.createdAt = createdAt;
    }
    // Getters et Setters...
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getAuthorInitials() { return authorInitials; }
    public void setAuthorInitials(String authorInitials) { this.authorInitials = authorInitials; }
    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}