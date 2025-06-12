package com.example.systeme_suivi_ticket.security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class CustomLoginSuccessHandlerTest {

	private CustomLoginSuccessHandler successHandler;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Authentication authentication;

	@BeforeEach
	public void setUp() {
		successHandler = new CustomLoginSuccessHandler();
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		authentication = mock(Authentication.class);
	}

	@Test
	public void testOnAuthenticationSuccess_AdminRole_RedirectsToAdminDashboard() throws IOException, ServletException {
		// Arrange
		Collection<? extends GrantedAuthority> authorities = Collections
				.singleton(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
		when(authentication.getAuthorities()).thenAnswer(inv -> authorities);

		// Act
		successHandler.onAuthenticationSuccess(request, response, authentication);

		// Assert
		verify(response).sendRedirect("/admin/dashboard");
	}

	@Test
	public void testOnAuthenticationSuccess_TechnicianRole_RedirectsToTechnicianDashboard()
			throws IOException, ServletException {
		// Arrange
		Collection<? extends GrantedAuthority> authorities = Collections
				.singleton(new SimpleGrantedAuthority("ROLE_TECHNICIAN"));
		when(authentication.getAuthorities()).thenAnswer(inv -> authorities);

		// Act
		successHandler.onAuthenticationSuccess(request, response, authentication);

		// Assert
		verify(response).sendRedirect("/technician/dashboard");
	}

	@Test
	public void testOnAuthenticationSuccess_DefaultRole_RedirectsToUserDashboard()
			throws IOException, ServletException {
		// Arrange
		Collection<? extends GrantedAuthority> authorities = Collections
				.singleton(new SimpleGrantedAuthority("ROLE_USER"));
		when(authentication.getAuthorities()).thenAnswer(inv -> authorities);

		// Act
		successHandler.onAuthenticationSuccess(request, response, authentication);

		// Assert
		verify(response).sendRedirect("/user/dashboard");
	}

	@Test
	public void testOnAuthenticationSuccess_MultipleRoles_PrioritizesAdmin() throws IOException, ServletException {
		// Arrange
		Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_TECHNICIAN"),
				new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
		when(authentication.getAuthorities()).thenAnswer(inv -> authorities);

		// Act
		successHandler.onAuthenticationSuccess(request, response, authentication);

		// Assert
		verify(response).sendRedirect("/admin/dashboard");
	}

	@Test
	public void testOnAuthenticationSuccess_NoAuthorities_RedirectsToUserDashboard()
			throws IOException, ServletException {
		// Arrange
		when(authentication.getAuthorities()).thenReturn(Collections.emptyList());

		// Act
		successHandler.onAuthenticationSuccess(request, response, authentication);

		// Assert
		verify(response).sendRedirect("/user/dashboard");
	}

	// Additional helper method for more complex test cases
	private Collection<? extends GrantedAuthority> createAuthorities(String... roles) {
		return Stream.of(roles).map(SimpleGrantedAuthority::new).toList();
	}
}