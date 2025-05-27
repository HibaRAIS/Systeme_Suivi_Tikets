package com.example.systeme_suivi_ticket.service.impl;

import com.example.systeme_suivi_ticket.model.User;
import com.example.systeme_suivi_ticket.repository.UserRepository;
import com.example.systeme_suivi_ticket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional // Good practice for methods that modify data
	public User registerNewUser(User user) {
		// Ensure password encoding and role setting are done before this point,
		// typically in the controller or a dedicated registration service method
		// that calls this.
		if (userRepository.existsByUsername(user.getUsername())) {
			throw new RuntimeException("Error: Username is already taken!");
		}
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new RuntimeException("Error: Email is already in use!");
		}
		return userRepository.save(user);
	}

	@Override
	@Transactional(readOnly = true) // Good practice for read-only methods
	public User findByEmail(String email) {
		return userRepository.findByEmail(email).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public User findByUsername(String username) {
		return userRepository.findByUsername(username).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}
}