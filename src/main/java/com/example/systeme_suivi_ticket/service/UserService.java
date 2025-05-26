package com.example.systeme_suivi_ticket.service;

import com.example.systeme_suivi_ticket.model.User;

public interface UserService {
	User registerNewUser(User user);

	User findByEmail(String email);

	User findByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);
}