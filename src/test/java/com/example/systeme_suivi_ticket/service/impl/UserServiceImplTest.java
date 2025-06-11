package com.example.systeme_suivi_ticket.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.example.systeme_suivi_ticket.model.User;
import com.example.systeme_suivi_ticket.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userService;

	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setId(1L);
		testUser.setUsername("testuser");
		testUser.setEmail("test@example.com");
		testUser.setPassword("encodedPassword");
	}

	// Register New User Tests
	@Test
	public void testRegisterNewUser_Success() {
		// Arrange
		when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(false);
		when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
		when(userRepository.save(testUser)).thenReturn(testUser);

		// Act
		User registeredUser = userService.registerNewUser(testUser);

		// Assert
		assertNotNull(registeredUser);
		assertEquals(testUser, registeredUser);
		verify(userRepository).save(testUser);
	}

	@Test
	public void testRegisterNewUser_UsernameTaken_ThrowsException() {
		// Arrange
		when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(true);

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.registerNewUser(testUser));

		assertEquals("Error: Username is already taken!", exception.getMessage());
		verify(userRepository, never()).save(any());
	}

	@Test
	public void testRegisterNewUser_EmailTaken_ThrowsException() {
		// Arrange
		when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(false);
		when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.registerNewUser(testUser));

		assertEquals("Error: Email is already in use!", exception.getMessage());
		verify(userRepository, never()).save(any());
	}

	@Test
	public void testRegisterNewUser_DatabaseError_ThrowsException() {
		// Arrange
		when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(false);
		when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
		when(userRepository.save(testUser)).thenThrow(new DataIntegrityViolationException("DB error"));

		// Act & Assert
		assertThrows(DataIntegrityViolationException.class, () -> userService.registerNewUser(testUser));
	}

	// Find By Email Tests
	@Test
	public void testFindByEmail_UserExists_ReturnsUser() {
		// Arrange
		when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

		// Act
		User foundUser = userService.findByEmail(testUser.getEmail());

		// Assert
		assertEquals(testUser, foundUser);
	}

	@Test
	public void testFindByEmail_UserNotExists_ReturnsNull() {
		// Arrange
		when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

		// Act
		User foundUser = userService.findByEmail("nonexistent@example.com");

		// Assert
		assertNull(foundUser);
	}

	// Find By Username Tests
	@Test
	public void testFindByUsername_UserExists_ReturnsUser() {
		// Arrange
		when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

		// Act
		User foundUser = userService.findByUsername(testUser.getUsername());

		// Assert
		assertEquals(testUser, foundUser);
	}

	@Test
	public void testFindByUsername_UserNotExists_ReturnsNull() {
		// Arrange
		when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

		// Act
		User foundUser = userService.findByUsername("nonexistent");

		// Assert
		assertNull(foundUser);
	}

	// Exists By Email Tests
	@Test
	public void testExistsByEmail_UserExists_ReturnsTrue() {
		// Arrange
		when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

		// Act
		boolean exists = userService.existsByEmail(testUser.getEmail());

		// Assert
		assertTrue(exists);
	}

	@Test
	public void testExistsByEmail_UserNotExists_ReturnsFalse() {
		// Arrange
		when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

		// Act
		boolean exists = userService.existsByEmail("nonexistent@example.com");

		// Assert
		assertFalse(exists);
	}

	// Exists By Username Tests
	@Test
	public void testExistsByUsername_UserExists_ReturnsTrue() {
		// Arrange
		when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(true);

		// Act
		boolean exists = userService.existsByUsername(testUser.getUsername());

		// Assert
		assertTrue(exists);
	}

	@Test
	public void testExistsByUsername_UserNotExists_ReturnsFalse() {
		// Arrange
		when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

		// Act
		boolean exists = userService.existsByUsername("nonexistent");

		// Assert
		assertFalse(exists);
	}
}