package com.example.systeme_suivi_ticket.security;

import java.io.IOException;
import java.util.Collection; // Import Collection

import com.example.systeme_suivi_ticket.repository.UserRepository;
import com.example.systeme_suivi_ticket.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority; // Import GrantedAuthority
// import org.springframework.security.core.userdetails.User; // Spring Security's built-in User - not needed if checking authorities directly
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // Ensure this is a Spring component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {


	private final UserRepository userRepository;
	private final UserServiceImpl userService;

	@Autowired
	public CustomLoginSuccessHandler(UserRepository userRepository, UserServiceImpl userService) {
		this.userRepository = userRepository;
		this.userService = userService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		String username = authentication.getName();
		System.out.println("Login success handler triggered for user: " + username);

		userRepository.findByUsername(username).ifPresentOrElse(
				user -> {
					System.out.println("User found with username: " + username);
					userService.updateLastLogin(user);
				},
				() -> System.out.println("No user found with username: " + username)
		);

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		boolean isAdmin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR")); // Matches
																												// CustomUserDetailsService
		boolean isTechnician = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_TECHNICIAN")); // Matches
																												// CustomUserDetailsService
		// boolean isUser = authorities.stream() // Not strictly needed if it's the
		// default
		// .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

		if (isAdmin) {
			response.sendRedirect("/admin/dashboard");
		} else if (isTechnician) {
			response.sendRedirect("/technician/dashboard"); // Or /technician/dashboard if you have a controller mapping
		} else { // Default to user dashboard
			response.sendRedirect("/user/dashboard"); // Or /user/dashboard
		}
	}
}