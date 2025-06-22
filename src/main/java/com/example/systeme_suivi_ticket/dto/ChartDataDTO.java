package com.example.systeme_suivi_ticket.dto;

import java.util.List;

public class ChartDataDTO {
    private List<String> labels; // Ex: ["Jan", "Feb", "Mar"]
    private List<Long> data;     // Ex: [65, 59, 80]

    public ChartDataDTO(List<String> labels, List<Long> data) {
        this.labels = labels;
        this.data = data;
    }

    // Getters
    public List<String> getLabels() { return labels; }
    public List<Long> getData() { return data; }
}
