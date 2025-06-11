package com.example.systeme_suivi_ticket.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.systeme_suivi_ticket.model.Roles;
import com.example.systeme_suivi_ticket.model.User;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository rolesRepository;

	@Test
	void testExistsByEmail() {
		Roles role = rolesRepository.save(new Roles("USER"));
		User user = new User();
		user.setUsername("zineb");
		user.setEmail("zineb@example.com");
		user.setPassword("password");
		user.setRole(role);
		userRepository.save(user);

		assertThat(userRepository.existsByEmail("zineb@example.com")).isTrue();
		assertThat(userRepository.existsByEmail("notfound@example.com")).isFalse();
	}

	@Test
	void testFindByUsername() {
		Roles role = rolesRepository.save(new Roles("ADMIN"));
		User user = new User();
		user.setUsername("admin");
		user.setEmail("admin@example.com");
		user.setPassword("password");
		user.setRole(role);
		userRepository.save(user);

		Optional<User> found = userRepository.findByUsername("admin");
		assertThat(found).isPresent();
		assertThat(found.get().getEmail()).isEqualTo("admin@example.com");
	}

}
