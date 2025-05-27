package com.example.systeme_suivi_ticket.repository;

import com.example.systeme_suivi_ticket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);
	Optional<User> findByUsername(String username); // Changed to Optional and correct method name
	boolean existsByUsername(String username); // Added
	Optional<User> findByEmail(String email); // Added
}