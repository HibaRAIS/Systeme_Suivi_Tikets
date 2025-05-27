package com.example.systeme_suivi_ticket.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Roles {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Good for auto-incrementing Long IDs
	@Column(name = "role_id")
	private Long id; // Stays Long

	@Column(name = "role_name", nullable = false, unique = true) // Added nullable and unique constraints
	private String roleName;

	// Constructors
	public Roles() {
	}

	public Roles(String roleName) {
		this.roleName = roleName;
	}

	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public String toString() {
		return "Roles{" +
				"id=" + id +
				", roleName='" + roleName + '\'' +
				'}';
	}
}