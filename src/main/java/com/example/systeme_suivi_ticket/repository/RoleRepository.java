package com.example.systeme_suivi_ticket.repository;

import com.example.systeme_suivi_ticket.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Import Optional

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> { // Changed Integer to Long
    Optional<Roles> findByRoleName(String roleName); // Useful for fetching roles by name
}