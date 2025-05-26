package com.example.systeme_suivi_ticket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authorize -> authorize.requestMatchers("/admin/**").hasAuthority("Administrator") // or
																															// hasRole("ADMIN")
						.requestMatchers("/auth/**").permitAll().anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/auth/login")
						.successHandler(new CustomAuthenticationSuccessHandler()).permitAll())
				.logout(logout -> logout.logoutUrl("/auth/logout").logoutSuccessUrl("/auth/login"));

		return http.build();
	}
}