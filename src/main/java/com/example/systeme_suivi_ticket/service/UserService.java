package com.example.systeme_suivi_ticket.service;

import com.example.systeme_suivi_ticket.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
	User registerNewUser(User user);

	User findByEmail(String email);

	User findByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	List<User> getAllUsers();

	Optional<User> getUserById(Long id);

	User saveUser(User user);

	User updateUser(User user);

	void deleteUser(Long id);

	public void updateLastLogin(User user);

	long countTotalUsers();

	long countActiveUsers();

	long countInactiveUsers();

	long countUsersByRoleName(String roleName);

	List<User> findUsersByFilters(Integer userType, Boolean userStatus, String searchTerm);

	List<User> findRecentUsers();
}