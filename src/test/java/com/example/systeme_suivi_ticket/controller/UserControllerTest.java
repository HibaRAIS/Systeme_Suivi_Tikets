package com.example.systeme_suivi_ticket.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import com.example.systeme_suivi_ticket.service.UserService;

@ActiveProfiles("test")
class UserControllerTest {

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void checkEmailExists_ShouldReturnTrue_WhenEmailExists() {
		when(userService.existsByEmail("test@example.com")).thenReturn(true);

		boolean result = userController.checkEmailExists("test@example.com");

		assertTrue(result);
		verify(userService).existsByEmail("test@example.com");
	}

	@Test
	void checkEmailExists_ShouldReturnFalse_WhenEmailIsEmpty() {
		boolean result = userController.checkEmailExists("");

		assertFalse(result);
		verify(userService, never()).existsByEmail(any());
	}

	@Test
	void checkUsernameExists_ShouldReturnTrue_WhenUsernameExists() {
		when(userService.existsByUsername("zineb")).thenReturn(true);

		boolean result = userController.checkUsernameExists("zineb");

		assertTrue(result);
		verify(userService).existsByUsername("zineb");
	}

	@Test
	void checkUsernameExists_ShouldReturnFalse_WhenUsernameIsNull() {
		boolean result = userController.checkUsernameExists(null);

		assertFalse(result);
		verify(userService, never()).existsByUsername(any());
	}
}
