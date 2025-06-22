package com.example.systeme_suivi_ticket.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

	@Column(name = "active", nullable = false, columnDefinition = "BIT(1) DEFAULT b'1'")
	private Boolean active = true;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(name = "username", nullable = false, unique = true) // Added constraints
	private String username;

	@Column(name = "email", nullable = false, unique = true) // Added constraints
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@ManyToOne
	@JoinColumn(name = "role_id", nullable = false) // Role should not be nullable
	private Roles role;

	@Column(name = "created_date", updatable = false)
	@CreationTimestamp
	private LocalDateTime createdDate;

	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	@OneToMany(mappedBy = "user")
	private List<TicketComment> comments;

	@OneToMany(mappedBy = "changedBy")
	private List<TicketStatusHistory> statusChanges;

	// Standard getters and setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Roles getRole() {
		return role;
	}

	// Setter for Roles (expects a managed Roles entity)
	public void setRole(Roles role) {
		this.role = role;
	}

	// REMOVED: public void setRole(String roleName)

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<TicketComment> getComments() {
		return comments;
	}

	public void setComments(List<TicketComment> comments) {
		this.comments = comments;
	}

	public List<TicketStatusHistory> getStatusChanges() {
		return statusChanges;
	}

	public void setStatusChanges(List<TicketStatusHistory> statusChanges) {
		this.statusChanges = statusChanges;
	}

}