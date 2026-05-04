package com.qalab.qawebpractice.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.qalab.qawebpractice.dto.RegistrationForm;
import com.qalab.qawebpractice.service.EmailAlreadyExistsException;
import com.qalab.qawebpractice.service.RegistrationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;

@Controller
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	private final RegistrationService registrationService;

	public AuthController(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	@GetMapping("/register")
	public String registerForm(Model model, Authentication authentication) {
		if (isLoggedIn(authentication)) {
			return "redirect:/dashboard";
		}

		if (!model.containsAttribute("registrationForm")) {
			model.addAttribute("registrationForm", new RegistrationForm());
		}

		return "register";
	}

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			logger.warn("Errores de validacion en registro: {}", bindingResult.getAllErrors());
			return "register";
		}

		try {
			registrationService.registerUser(registrationForm);
			logger.info("Usuario registrado exitosamente: {}", registrationForm.getEmail());
		} catch (EmailAlreadyExistsException exception) {
			logger.warn("Intento de registro con email duplicado: {}", registrationForm.getEmail());
			bindingResult.rejectValue("email", "duplicate", exception.getMessage());
			return "register";
		}

		return "redirect:/login?registered";
	}

	@GetMapping("/login")
	public String login(Authentication authentication) {
		if (isLoggedIn(authentication)) {
			return "redirect:/dashboard";
		}

		return "login";
	}

	private boolean isLoggedIn(Authentication authentication) {
		return authentication != null
			&& authentication.isAuthenticated()
			&& !(authentication instanceof AnonymousAuthenticationToken);
	}
}
