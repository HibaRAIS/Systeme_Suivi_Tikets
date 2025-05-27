package com.example.systeme_suivi_ticket.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Import Optional

import org.springframework.beans.factory.annotation.Autowired; // Import Autowired
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import com.example.systeme_suivi_ticket.model.User;
import com.example.systeme_suivi_ticket.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Autowired // Constructor injection
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true) // Good practice for read-only service methods
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Load the user from the database by username
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

		List<GrantedAuthority> authorities = new ArrayList<>();
		// Assuming Role entity has a getRoleName() method
		// Spring Security's hasRole() method automatically prepends "ROLE_"
		// So, if roleName is "ADMIN", hasRole("ADMIN") will work.
		// If roleName is "ROLE_ADMIN", then hasRole("ADMIN") will also work.
		// For clarity, it's common to store roles as "ADMIN", "USER", etc.,
		// and let Spring Security handle the "ROLE_" prefix internally or add it here explicitly.
		// Your CustomLoginSuccessHandler checks for "ROLE_ADMINISTRATOR", "ROLE_TECHNICIAN"
		// So make sure getRoleName() returns "ADMINISTRATOR", "TECHNICIAN" etc.
		// and then add "ROLE_" prefix here.
		authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName().toUpperCase()));

		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPassword(),
				authorities);
	}
}