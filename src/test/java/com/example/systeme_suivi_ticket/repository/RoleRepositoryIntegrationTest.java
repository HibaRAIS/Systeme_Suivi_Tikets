package com.example.systeme_suivi_ticket.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.systeme_suivi_ticket.model.Roles;

@DataJpaTest
@ActiveProfiles("test")
class RoleRepositoryIntegrationTest {

	@Autowired
	private RoleRepository roleRepository;

	@Test
	void testFindByRoleName_ReturnsRole() {
		// Arrange
		Roles savedRole = roleRepository.save(new Roles("USER"));

		// Act
		Optional<Roles> result = roleRepository.findByRoleName("USER");

		// Assert
		assertThat(result).isPresent();
		assertThat(result.get().getRoleName()).isEqualTo("USER");
	}

	@Test
	void testFindByRoleName_ReturnsEmptyIfNotFound() {
		Optional<Roles> result = roleRepository.findByRoleName("NON_EXISTENT");

		assertThat(result).isNotPresent();
	}

}
