package com.example.systeme_suivi_ticket.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ticketpriorities")
public class TicketPriority {
    @Id
    @Column(name = "PriorityID")
    private Integer priorityId;

    @Column(name = "PriorityName")
    private String priorityName;

    public Integer getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(Integer id) {
        this.priorityId = id;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String name) {
        this.priorityName = name;
    }

}

