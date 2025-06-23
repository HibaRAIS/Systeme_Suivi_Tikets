package com.example.systeme_suivi_ticket.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

public class HistoryEntryDTO {
    private String description;
    private String author;
    private String timestamp;

    @JsonIgnore // Champ technique pour le tri, on ne l'envoie pas au frontend
    private LocalDateTime rawTimestamp;

    // Constructeurs, Getters et Setters
    public HistoryEntryDTO(String description, String author, String timestamp, LocalDateTime rawTimestamp) {
        this.description = description;
        this.author = author;
        this.timestamp = timestamp;
        this.rawTimestamp = rawTimestamp;
    }

    public String getDescription() { return description; }
    public String getAuthor() { return author; }
    public String getTimestamp() { return timestamp; }
    public LocalDateTime getRawTimestamp() { return rawTimestamp; }
}
