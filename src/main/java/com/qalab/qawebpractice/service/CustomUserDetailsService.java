package com.qalab.qawebpractice.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.qalab.qawebpractice.model.UserAccount;
import com.qalab.qawebpractice.repository.UserAccountRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserAccountRepository userAccountRepository;

	public CustomUserDetailsService(UserAccountRepository userAccountRepository) {
		this.userAccountRepository = userAccountRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserAccount userAccount = userAccountRepository.findByEmailIgnoreCase(email)
			.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));

		return User.withUsername(userAccount.getEmail())
			.password(userAccount.getPassword())
			.roles(userAccount.getRole())
			.build();
	}
}
