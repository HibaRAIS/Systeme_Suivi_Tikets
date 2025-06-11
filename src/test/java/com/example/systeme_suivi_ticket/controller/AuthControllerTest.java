package com.example.systeme_suivi_ticket.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.example.systeme_suivi_ticket.dto.RegistrationRequest;
import com.example.systeme_suivi_ticket.model.Roles;
import com.example.systeme_suivi_ticket.model.User;
import com.example.systeme_suivi_ticket.repository.RoleRepository;
import com.example.systeme_suivi_ticket.repository.UserRepository;

class AuthControllerTest {

	@InjectMocks
	private AuthController authController;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testRegisterProcess_Success() {
		// Arrange
		RegistrationRequest req = new RegistrationRequest();
		req.setUsername("newuser");
		req.setEmail("newuser@example.com");
		req.setPassword("password123");
		req.setFirstName("zineb");
		req.setLastName("saidi");
		req.setRoleId(1L);

		BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "registrationRequest");
		Model model = mock(Model.class);
		RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

		when(userRepository.existsByUsername("newuser")).thenReturn(false);
		when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);

		Roles role = new Roles();
		role.setId(1L);
		role.setRoleName("Admin");

		when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
		when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

		// Act
		String view = authController.registerProcess(req, bindingResult, model, redirectAttributes);

		// Assert
		assertEquals("redirect:/auth/login", view);
		assertTrue(redirectAttributes.getFlashAttributes().containsKey("message"));
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void testRegisterProcess_ValidationErrors() {
		// Arrange
		RegistrationRequest req = new RegistrationRequest();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);

		Model model = mock(Model.class);

		// Act
		String view = authController.registerProcess(req, bindingResult, model, mock(RedirectAttributes.class));

		// Assert
		assertEquals("auth/Register", view);
		verify(model, times(1)).addAttribute(eq("roles"), any());
		verify(userRepository, never()).save(any());
	}

	@Test
	void testRegisterProcess_UsernameExists() {
		// Arrange
		RegistrationRequest req = new RegistrationRequest();
		req.setUsername("existingUser");
		req.setEmail("newemail@example.com");
		BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "registrationRequest");
		Model model = mock(Model.class);

		when(userRepository.existsByUsername("existingUser")).thenReturn(true);
		when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
		when(roleRepository.findAll()).thenReturn(Collections.emptyList());

		// Act
		String view = authController.registerProcess(req, bindingResult, model, mock(RedirectAttributes.class));

		// Assert
		assertTrue(bindingResult.hasErrors());
		assertEquals("auth/Register", view);
		verify(userRepository, never()).save(any());
	}

	@Test
	void testRegisterProcess_EmailExists() {
		// Arrange
		RegistrationRequest req = new RegistrationRequest();
		req.setUsername("newuser");
		req.setEmail("existingemail@example.com");
		BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "registrationRequest");
		Model model = mock(Model.class);

		when(userRepository.existsByUsername("newuser")).thenReturn(false);
		when(userRepository.existsByEmail("existingemail@example.com")).thenReturn(true);
		when(roleRepository.findAll()).thenReturn(Collections.emptyList());

		// Act
		String view = authController.registerProcess(req, bindingResult, model, mock(RedirectAttributes.class));

		// Assert
		assertTrue(bindingResult.hasErrors());
		assertEquals("auth/Register", view);
		verify(userRepository, never()).save(any());
	}

	@Test
	void testRegisterProcess_ExceptionDuringSave() {
		// Arrange
		RegistrationRequest req = new RegistrationRequest();
		req.setUsername("newuser");
		req.setEmail("newuser@example.com");
		req.setPassword("password123");
		req.setFirstName("zineb");
		req.setLastName("saidi");

		BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "registrationRequest");
		Model model = mock(Model.class);
		RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

		when(userRepository.existsByUsername("newuser")).thenReturn(false);
		when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);

		Roles role = new Roles();
		role.setRoleName("User");
		when(roleRepository.findByRoleName("User")).thenReturn(Optional.of(role));

		when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

		doThrow(new RuntimeException("DB error")).when(userRepository).save(any(User.class));

		// Act
		String view = authController.registerProcess(req, bindingResult, model, redirectAttributes);

		// Assert
		assertEquals("redirect:/auth/register", view);
		assertTrue(redirectAttributes.getFlashAttributes().containsKey("error"));
		assertTrue(redirectAttributes.getFlashAttributes().containsKey("registrationRequest"));
	}
}
