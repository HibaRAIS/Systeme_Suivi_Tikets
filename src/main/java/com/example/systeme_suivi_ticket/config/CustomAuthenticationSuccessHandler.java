package com.example.systeme_suivi_ticket.config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		boolean isAdmin = authorities.stream()
				.anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN") || ga.getAuthority().equals("Administrator"));
		if (isAdmin) {
			response.sendRedirect("/admin/dashboard");
		} else {
			response.sendRedirect("/user/dashboard");
		}
	}
}