package com.example.systeme_suivi_ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Import BindingResult
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Import RedirectAttributes

import com.example.systeme_suivi_ticket.dto.RegistrationRequest;
import com.example.systeme_suivi_ticket.model.Roles; // Import Roles
import com.example.systeme_suivi_ticket.model.User;
import com.example.systeme_suivi_ticket.repository.RoleRepository;
import com.example.systeme_suivi_ticket.repository.UserRepository;

import jakarta.validation.Valid; // Import Valid

@Controller
public class AuthController {

	@Autowired // Autowire UserRepository
	private UserRepository userRepository;

	@Autowired // Autowire RoleRepository
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/*
	 * @GetMapping("/auth/login") public String login(Model
	 * model, @ModelAttribute("error") String error, @ModelAttribute("message")
	 * String message) { // Add attributes to model if they are present from
	 * redirect if (error != null && !error.isEmpty()) { model.addAttribute("error",
	 * error); } if (message != null && !message.isEmpty()) {
	 * model.addAttribute("message", message); } return "auth/Login"; // Ensure
	 * template name matches case }
	 */
	@GetMapping("/auth/login")
	public String login(Model model, @RequestParam(name = "error", required = false) String error,
			@RequestParam(name = "message", required = false) String message) {

		// Now, 'error' and 'message' will be null on a direct visit.
		// This code will only execute if a redirect added these as URL parameters.
		// e.g., "redirect:/auth/login?error=Invalid+Credentials"

		if (error != null && !error.isEmpty()) {
			// You might want a more user-friendly message here instead of just "true" or
			// the exception text
			model.addAttribute("error", "Invalid username or password.");
		}
		if (message != null && !message.isEmpty()) {
			model.addAttribute("message", message);
		}

		return "auth/Login";
	}

	@GetMapping("/auth/register")
	public String register(Model model) {
		if (!model.containsAttribute("registrationRequest")) {
			model.addAttribute("registrationRequest", new RegistrationRequest());
		}
		try {
			model.addAttribute("roles", roleRepository.findAll());
		} catch (Exception e) {
			// Log the exception e
			model.addAttribute("error", "Unable to load roles for registration form. Please try again later.");
			// Optionally, redirect to an error page or return the register page with fewer
			// options
		}
		return "auth/Register"; // Ensure template name matches case
	}

	@PostMapping("/auth/register-process")
	public String registerProcess(@Valid @ModelAttribute("registrationRequest") RegistrationRequest registrationRequest,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("roles", roleRepository.findAll()); // Reload roles for the form
			return "auth/Register";
		}

		if (userRepository.existsByUsername(registrationRequest.getUsername())) {
			bindingResult.rejectValue("username", "error.user", "Username already in use.");
		}
		if (userRepository.existsByEmail(registrationRequest.getEmail())) {
			bindingResult.rejectValue("email", "error.user", "Email already in use.");
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("roles", roleRepository.findAll());
			return "auth/Register";
		}

		try {
			User user = new User();
			user.setUsername(registrationRequest.getUsername());
			user.setEmail(registrationRequest.getEmail());
			user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
			user.setFirstName(registrationRequest.getFirstName());
			user.setLastName(registrationRequest.getLastName());

			Long roleId = registrationRequest.getRoleId();
			Roles assignedRole;
			if (roleId != null) {
				assignedRole = roleRepository.findById(roleId)
						.orElseThrow(() -> new RuntimeException("Error: Selected role not found."));
			} else {
				// Default to "USER" role (assuming ID 2L is 'USER', or find by name)
				assignedRole = roleRepository.findByRoleName("User") // Or "USER" depending on SQL data
						.orElseThrow(() -> new RuntimeException(
								"Error: Default User role not found. Please ensure it exists in the database."));
			}
			user.setRole(assignedRole);

			userRepository.save(user);
			redirectAttributes.addFlashAttribute("message", "Registration successful. Please log in.");
			return "redirect:/auth/login";

		} catch (Exception e) {
			// Log the exception e
			redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
			// Add registrationRequest back to flash attributes to repopulate form on
			// redirect
			redirectAttributes.addFlashAttribute("registrationRequest", registrationRequest);
			return "redirect:/auth/register"; // Redirect to avoid form resubmission issues
		}
	}
}