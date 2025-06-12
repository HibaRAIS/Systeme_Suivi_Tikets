package com.example.systeme_suivi_ticket.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.systeme_suivi_ticket.model.Roles;
import com.example.systeme_suivi_ticket.model.User;
import com.example.systeme_suivi_ticket.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CustomUserDetailsService userDetailsService;

	private User testUser;

	@BeforeEach
	void setUp() {
		Roles adminRole = new Roles();
		adminRole.setRoleName("ADMINISTRATOR");

		testUser = new User();
		testUser.setUsername("admin");
		testUser.setPassword("encodedPassword");
		testUser.setRole(adminRole);
	}

	@Test
	public void testLoadUserByUsername_UserFound_ReturnsUserDetails() {
		// Arrange
		when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));

		// Act
		UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

		// Assert
		assertNotNull(userDetails);
		assertEquals("admin", userDetails.getUsername());
		assertEquals("encodedPassword", userDetails.getPassword());
		assertTrue(userDetails.getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATOR")));
	}

	@Test
	public void testLoadUserByUsername_UserNotFound_ThrowsException() {
		// Arrange
		when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

		// Act & Assert
		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
				() -> userDetailsService.loadUserByUsername("unknown"));

		assertEquals("User not found with username: unknown", exception.getMessage());
	}

	@Test
	public void testLoadUserByUsername_DifferentRole_ReturnsCorrectAuthority() {
		// Arrange
		Roles techRole = new Roles();
		techRole.setRoleName("TECHNICIAN");
		testUser.setRole(techRole);
		when(userRepository.findByUsername("tech")).thenReturn(Optional.of(testUser));

		// Act
		UserDetails userDetails = userDetailsService.loadUserByUsername("tech");

		// Assert
		assertTrue(
				userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_TECHNICIAN")));
	}

	@Test
	public void testLoadUserByUsername_RoleNameCaseInsensitive() {
		// Arrange
		Roles role = new Roles();
		role.setRoleName("administrator"); // lowercase
		testUser.setRole(role);
		when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));

		// Act
		UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

		// Assert
		assertTrue(userDetails.getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATOR")));
	}
}