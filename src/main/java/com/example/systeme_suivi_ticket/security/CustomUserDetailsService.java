package com.example.systeme_suivi_ticket.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.systeme_suivi_ticket.model.User;
import com.example.systeme_suivi_ticket.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Load the user from the database
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}

		// Convert the role stored by foreign key to a GrantedAuthority
		List<GrantedAuthority> authorities = new ArrayList<>();
		// Assuming Role entity has a getRoleName() method that returns "User",
		// "Administrator", etc.
		authorities.add(new SimpleGrantedAuthority(user.getRole().getRoleName()));

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				authorities);
	}
}