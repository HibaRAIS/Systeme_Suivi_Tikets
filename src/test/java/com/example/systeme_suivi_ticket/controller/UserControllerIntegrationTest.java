package com.example.systeme_suivi_ticket.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.example.systeme_suivi_ticket.service.UserService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class UserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private PasswordEncoder passwordEncoder;

	@Test
	void checkEmailExists_ReturnsTrue() throws Exception {
		when(userService.existsByEmail("test@example.com")).thenReturn(true);

		mockMvc.perform(get("/register/check-email").param("email", "test@example.com")).andExpect(status().isOk())
				.andExpect(content().string("true"));
	}

	@Test
	void checkUsernameExists_ReturnsFalse() throws Exception {
		when(userService.existsByUsername("zineb")).thenReturn(false);

		mockMvc.perform(get("/register/check-username").param("username", "zineb")).andExpect(status().isOk())
				.andExpect(content().string("false"));
	}

	@Test
	void showRegisterForm_ShouldRedirect() throws Exception {
		mockMvc.perform(get("/register")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/auth/register"));
	}

	@Test
	void showUserDashboard_ShouldReturnView() throws Exception {
		mockMvc.perform(get("/user/dashboard")).andExpect(status().isOk()).andExpect(view().name("user/Dashboard"));
	}
}