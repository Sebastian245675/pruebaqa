package com.qalab.qawebpractice.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.qalab.qawebpractice.dto.RegistrationForm;
import com.qalab.qawebpractice.model.UserAccount;
import com.qalab.qawebpractice.repository.UserAccountRepository;

@Service
public class RegistrationService {

	private final UserAccountRepository userAccountRepository;
	private final PasswordEncoder passwordEncoder;

	public RegistrationService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
		this.userAccountRepository = userAccountRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public UserAccount registerUser(RegistrationForm form) {
		String normalizedEmail = normalizeEmail(form.getEmail());

		if (userAccountRepository.existsByEmailIgnoreCase(normalizedEmail)) {
			throw new EmailAlreadyExistsException("Ya existe una cuenta registrada con ese correo.");
		}

		UserAccount userAccount = new UserAccount();
		userAccount.setFullName(form.getFullName().trim());
		userAccount.setEmail(normalizedEmail);
		userAccount.setPassword(passwordEncoder.encode(form.getPassword()));
		userAccount.setRole("USER");

		return userAccountRepository.save(userAccount);
	}

	public UserAccount findByEmail(String email) {
		return userAccountRepository.findByEmailIgnoreCase(email)
			.orElseThrow(() -> new IllegalArgumentException("No se encontro el usuario autenticado."));
	}

	private String normalizeEmail(String email) {
		return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
	}
}
