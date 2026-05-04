package com.qalab.qawebpractice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize
			.requestMatchers("/", "/register", "/login", "/css/**", "/h2-console/**").permitAll()
			.anyRequest().authenticated());

		http.formLogin(form -> form
			.loginPage("/login")
			.usernameParameter("email")
			.defaultSuccessUrl("/dashboard", true)
			.failureUrl("/login?error")
			.permitAll());

		http.logout(logout -> logout
			.logoutSuccessUrl("/login?logout")
			.permitAll());

		http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
		http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
