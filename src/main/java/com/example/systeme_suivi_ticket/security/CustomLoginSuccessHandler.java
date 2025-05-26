package com.example.systeme_suivi_ticket.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User; // Spring Security's built-in User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		User springSecurityUser = (User) authentication.getPrincipal();

		// If you're using the default 'User', you typically only get username +
		// authorities,
		// not a custom 'roleId'.
		// So you'd have to base your redirect on the authorities themselves:
		// e.g. "ROLE_ADMINISTRATOR", "ROLE_TECHNICIAN", "ROLE_USER"

		boolean isAdmin = springSecurityUser.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR"));
		boolean isTechnician = springSecurityUser.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_TECHNICIAN"));

		if (isAdmin) {
			response.sendRedirect("/admin/dashboard");
		} else if (isTechnician) {
			response.sendRedirect("/technician/dashboard");
		} else {
			response.sendRedirect("/user/dashboard");
		}
	}
}
