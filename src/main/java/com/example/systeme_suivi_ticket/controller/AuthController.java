package com.example.systeme_suivi_ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.systeme_suivi_ticket.dto.RegistrationRequest;
import com.example.systeme_suivi_ticket.model.User;
import com.example.systeme_suivi_ticket.repository.RoleRepository;
import com.example.systeme_suivi_ticket.repository.UserRepository;

@Controller
public class AuthController {

	private UserRepository userRepository;

	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/auth/login")
	public String login() {
		return "auth/login";
	}

	@GetMapping("/auth/register")
	public String register(Model model) {
		try {
			model.addAttribute("registrationRequest", new RegistrationRequest());
			model.addAttribute("roles", roleRepository.findAll());
			return "auth/register";
		} catch (Exception e) {
			model.addAttribute("error", "Unable to load registration form. Please try again later.");
			return "error";
		}
	}

	@PostMapping("/auth/register-process")
	public String registerProcess(@ModelAttribute RegistrationRequest registrationRequest, Model model) {
		try {
			if (userRepository.existsByEmail(registrationRequest.getEmail())) {
				model.addAttribute("error", "Email already in use.");
				model.addAttribute("roles", roleRepository.findAll());
				return "auth/register";
			}

			User user = new User();
			user.setUsername(registrationRequest.getUsername());
			user.setEmail(registrationRequest.getEmail());
			user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
			user.setFirstName(registrationRequest.getFirstName());
			user.setLastName(registrationRequest.getLastName());

			// Convert Long to Integer using intValue() to match the repository's type
			if (registrationRequest.getRoleId() != null) {
				roleRepository.findById(registrationRequest.getRoleId().intValue()).ifPresent(user::setRole);
			} else {
				// Handle setting default role appropriately (casting may be needed if
				// User#setRole expects a Role)
				// For example, get default role from repository or use a constant
				user.setRole("USER");
			}

			userRepository.save(user);
			model.addAttribute("message", "Registration successful. Please log in.");
			return "auth/login";
		} catch (Exception e) {
			model.addAttribute("error", "Registration failed. Please try again later.");
			model.addAttribute("roles", roleRepository.findAll());
			return "auth/register";
		}
	}

}
