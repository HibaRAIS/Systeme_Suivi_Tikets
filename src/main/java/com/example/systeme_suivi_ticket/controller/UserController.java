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
import com.example.systeme_suivi_ticket.service.UserService;

import jakarta.validation.Valid;

@Controller
public class UserController {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserController(UserService userService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Shows the registration page
	 *
	 * @param model the model to be populated
	 * @return the register view name
	 */
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		if (!model.containsAttribute("user")) {
			model.addAttribute("user", new User());
		}
		return "register";
	}

	/**
	 * Processes the registration form submission
	 *
	 * @param user               the submitted user data
	 * @param bindingResult      validation results
	 * @param redirectAttributes redirect attributes for flash messages
	 * @return redirect to login on success or back to register on failure
	 */
	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		// Check for validation errors
		if (bindingResult.hasErrors()) {
			// Return the view directly to preserve field-level errors
			return "register";
		}

		// Check if email already exists
		if (userService.existsByEmail(user.getEmail())) {
			bindingResult.rejectValue("email", "error.user", "Email already in use");
			return "register";
		}

		// Set a default role if none is provided
		if (user.getRole() == null) {
			user.setRole("USER"); // Default role
		}

		// Attempt to save the user
		try {
			// Encode the password before saving
			String encodedPassword = passwordEncoder.encode(user.getPassword());
			user.setPassword(encodedPassword);

			userService.registerNewUser(user);
			redirectAttributes.addFlashAttribute("success",
					"Registration successful! Please log in with your credentials.");
			return "redirect:/login";
		} catch (Exception e) {
			bindingResult.reject("error.user", "Registration failed: " + e.getMessage());
			return "register";
		}
	}

	/**
	 * Check if email exists (for AJAX validation)
	 *
	 * @param email the email address to check
	 * @return true if the email exists, false otherwise
	 */
	@GetMapping("/register/check-email")
	@ResponseBody
	public boolean checkEmailExists(@RequestParam(required = true) String email) {
		if (email == null || email.trim().isEmpty()) {
			return false;
		}
		return userService.existsByEmail(email);
	}

	/**
	 * Check if username exists (for AJAX validation)
	 *
	 * @param username the username to check
	 * @return true if the username exists, false otherwise
	 */
	@GetMapping("/register/check-username")
	@ResponseBody
	public boolean checkUsernameExists(@RequestParam(required = true) String username) {
		if (username == null || username.trim().isEmpty()) {
			return false;
		}
		return userService.existsByEmail(username); // Changed to use name instead of username
	}
}
