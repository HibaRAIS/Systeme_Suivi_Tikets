package com.example.systeme_suivi_ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.systeme_suivi_ticket.model.User;
// import com.example.systeme_suivi_ticket.model.Roles; // If using RoleRepository here
// import com.example.systeme_suivi_ticket.repository.RoleRepository; // If using RoleRepository here
import com.example.systeme_suivi_ticket.service.UserService;

import jakarta.validation.Valid;

@Controller
public class UserController {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	// @Autowired // If used here
	// private RoleRepository roleRepository;

	@Autowired
	public UserController(UserService userService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Shows the registration page - This might be redundant if /auth/register is primary
	 */
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		// This endpoint might conflict with AuthController's /auth/register
		// Consider redirecting or ensuring it uses the same DTO and logic if kept
		// For now, assuming AuthController is the main registration point.
		// If this is still needed, ensure "user" object is RegistrationRequest DTO
		// and roles are loaded similar to AuthController.
		// model.addAttribute("registrationRequest", new RegistrationRequest());
		// model.addAttribute("roles", roleRepository.findAll());
		return "redirect:/auth/register"; // Redirect to the main registration form
	}

	/**
	 * Processes the registration form submission - This might be redundant
	 */
	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("userDTO") User user, // Assuming User DTO, ideally RegistrationRequest
							   BindingResult bindingResult,
							   RedirectAttributes redirectAttributes, Model model) {
		// This endpoint also likely conflicts with AuthController's /auth/register-process
		// If this is used, it needs the same logic as AuthController for validation,
		// role fetching, password encoding, and saving.
		// For now, assuming AuthController is the main registration process.

		// Example minimal logic if kept (needs robust implementation like AuthController)
		if (bindingResult.hasErrors()) {
			// model.addAttribute("roles", roleRepository.findAll()); // Need roles if returning to form
			return "auth/Register"; // Or your specific template for this path
		}
		if (userService.existsByUsername(user.getUsername())) { // Use username
			bindingResult.rejectValue("username", "error.user", "Username already in use");
		}
		if (userService.existsByEmail(user.getEmail())) {
			bindingResult.rejectValue("email", "error.user", "Email already in use");
		}

		if (bindingResult.hasErrors()) {
			// model.addAttribute("roles", roleRepository.findAll());
			return "auth/Register";
		}

		// user.setPassword(passwordEncoder.encode(user.getPassword()));
		// Set role correctly by fetching from RoleRepository
		// e.g., Roles defaultRole = roleRepository.findByRoleName("User").orElseThrow(...);
		// user.setRole(defaultRole); // Pass the Roles entity

		// userService.registerNewUser(user); // This would use the UserServiceImpl
		redirectAttributes.addFlashAttribute("message", "Registration successful! Please log in.");
		return "redirect:/auth/login";
	}


	@GetMapping("/register/check-email")
	@ResponseBody
	public boolean checkEmailExists(@RequestParam(required = true) String email) {
		if (email == null || email.trim().isEmpty()) {
			return false;
		}
		return userService.existsByEmail(email);
	}

	@GetMapping("/register/check-username")
	@ResponseBody
	public boolean checkUsernameExists(@RequestParam(required = true) String username) {
		if (username == null || username.trim().isEmpty()) {
			return false;
		}
		return userService.existsByUsername(username); // Corrected to use existsByUsername
	}
}