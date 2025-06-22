package com.example.systeme_suivi_ticket.service.impl;

import com.example.systeme_suivi_ticket.model.User;
import com.example.systeme_suivi_ticket.repository.UserRepository;
import com.example.systeme_suivi_ticket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

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

	@Override
	@Transactional(readOnly = true)
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}


	@Override
	@Transactional
	public User saveUser(User user) {
		if (user.getPassword() != null && !user.getPassword().isEmpty()) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		return userRepository.save(user);
	}


	@Override
	@Transactional
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	public void updateLastLogin(User user) {
		System.out.println("Updating last login for user: " + user.getUsername());
		user.setLastLogin(LocalDateTime.now());
		userRepository.save(user);
		System.out.println("Last login updated.");
	}

	@Override
	@Transactional
	public User updateUser(User user) {
		Optional<User> existingOptional = userRepository.findById(user.getId());
		if (existingOptional.isPresent()) {
			User existingUser = existingOptional.get();

			// Mise à jour des champs
			existingUser.setUsername(user.getUsername());
			existingUser.setEmail(user.getEmail());
			existingUser.setFirstName(user.getFirstName());
			existingUser.setLastName(user.getLastName());
			existingUser.setActive(user.getActive());
			existingUser.setRole(user.getRole());

			if (user.getPassword() != null && !user.getPassword().isEmpty()) {
				existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
			}

			return userRepository.save(existingUser);
		}
		return null;
	}


	@Override
	@Transactional
	public long countTotalUsers() {
		return userRepository.count();
	}

	@Override
	@Transactional
	public long countActiveUsers() {
		return userRepository.countByActiveTrue();
	}

	@Override
	@Transactional
	public long countInactiveUsers() {
		return userRepository.countByActiveFalse();
	}

	@Override
	@Transactional
	public long countUsersByRoleName(String roleName) {
		return userRepository.countByRole_RoleName(roleName);
	}

	@Override
	@Transactional
	public List<User> findUsersByFilters(Integer userType, Boolean userStatus, String searchTerm)
	{
		if ((userType == null || userType == 0) && (userStatus == null) && (searchTerm == null || searchTerm.isEmpty())) {
			return userRepository.findAll();
		}
		return userRepository.findUsersByFilters(userType, userStatus, searchTerm);
	}

	@Override
	@Transactional
	public List<User> findRecentUsers() {
		return userRepository.findTop5ByOrderByCreatedDateDesc();
	}

}