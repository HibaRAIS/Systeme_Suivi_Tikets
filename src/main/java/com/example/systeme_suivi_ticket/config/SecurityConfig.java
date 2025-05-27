package com.example.systeme_suivi_ticket.config;

import com.example.systeme_suivi_ticket.security.CustomLoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // For CSRF disable if needed
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomLoginSuccessHandler customLoginSuccessHandler;

    // If you have a CustomUserDetailsService, Spring Boot typically autoconfigures it
    // with DaoAuthenticationProvider if a PasswordEncoder bean is also present.
    // Explicitly configuring AuthenticationManagerBuilder is less common in modern Spring Security.

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Consider CSRF protection strategy. Disabling is simpler for now but not recommended for production.
                // .csrf(AbstractHttpConfigurer::disable) // Example: Disable CSRF
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(
                                        AntPathRequestMatcher.antMatcher("/"), // Home page
                                        AntPathRequestMatcher.antMatcher("/test"),
                                        AntPathRequestMatcher.antMatcher("/auth/**"),    // Login, Register page, Register process
                                        AntPathRequestMatcher.antMatcher("/register"),   // If UserController /register is still used
                                        AntPathRequestMatcher.antMatcher("/register/check-email"),
                                        AntPathRequestMatcher.antMatcher("/register/check-username"),
                                        AntPathRequestMatcher.antMatcher("/css/**"),
                                        AntPathRequestMatcher.antMatcher("/js/**"),
                                        AntPathRequestMatcher.antMatcher("/images/**"),
                                        AntPathRequestMatcher.antMatcher("/webjars/**")  // For Bootstrap, etc. if used via webjars
                                ).permitAll()
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/admin/**")).hasRole("ADMINISTRATOR") // Matches CustomLoginSuccessHandler
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/technician/**")).hasAnyRole("TECHNICIAN", "ADMINISTRATOR") // Matches CustomLoginSuccessHandler
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/user/**")).hasAnyRole("USER", "TECHNICIAN", "ADMINISTRATOR")
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/auth/login")         // Your custom login page URL
                                .loginProcessingUrl("/auth/login") // The URL to submit the username and password to
                                .successHandler(customLoginSuccessHandler)
                                .failureUrl("/auth/login?error=true") // Redirect back to login page on failure
                                .permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/auth/login?logout")
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID") // If needed
                                .permitAll()
                );
        return http.build();
    }
}