package com.example.systeme_suivi_ticket.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "ticketstatuses")
public class TicketStatus {
    @Id
    @Column(name = "StatusID")
    private Integer statusId;

    @Column(name = "StatusName")
    private String statusName;

    @OneToMany(mappedBy = "status")
    private List<TicketStatusHistory> historyEntries;

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer id) {
        this.statusId = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String name) {
        this.statusName = name;
    }

    public List<TicketStatusHistory> getHistoryEntries() {
        return historyEntries;
    }

    public void setHistoryEntries(List<TicketStatusHistory> historyEntries) {
        this.historyEntries = historyEntries;
    }

}
