package com.example.systeme_suivi_ticket.dto;

public class TicketStatusChartData {
    private String statusName;
    private Long count;

    public TicketStatusChartData(String statusName, Long count) {
        this.statusName = statusName;
        this.count = count;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

