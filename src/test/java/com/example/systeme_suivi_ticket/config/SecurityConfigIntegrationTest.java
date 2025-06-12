package com.example.systeme_suivi_ticket.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void givenPublicUrl_whenAccessed_thenShouldReturnOk() throws Exception {
		mockMvc.perform(get("/auth/login")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = "USER")
	void givenUserRole_whenAccessUserUrl_thenAllowed() throws Exception {
		mockMvc.perform(get("/user/dashboard")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = "USER")
	void givenUserRole_whenAccessAdminUrl_thenForbidden() throws Exception {
		mockMvc.perform(get("/admin/dashboard")).andExpect(status().isForbidden());
	}
}
