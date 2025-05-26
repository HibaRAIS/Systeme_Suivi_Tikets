package com.example.systeme_suivi_ticket.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.systeme_suivi_ticket.model.User;
import com.example.systeme_suivi_ticket.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private final Map<String, User> emailUserMap = new HashMap<>();
	private final Map<String, User> usernameUserMap = new HashMap<>();

	@Override
	public User registerNewUser(User user) {
		// Save user using email and username; ensure user contains firstName and
		// lastName values.
		emailUserMap.put(user.getEmail(), user);
		usernameUserMap.put(user.getUsername(), user);
		return user;
	}

	@Override
	public User findByEmail(String email) {
		return emailUserMap.get(email);
	}

	@Override
	public User findByUsername(String username) {
		return usernameUserMap.get(username);
	}

	@Override
	public boolean existsByEmail(String email) {
		return emailUserMap.containsKey(email);
	}

	@Override
	public boolean existsByUsername(String username) {
		return usernameUserMap.containsKey(username);
	}
}