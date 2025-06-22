package com.example.systeme_suivi_ticket.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tickettypes")
public class TicketType {

    @Id
    @Column(name = "TypeID")
    private Integer typeId;

    @Column(name = "TypeName")
    private String typeName;

    // Getters and setters
    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer id) {
        this.typeId = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}

