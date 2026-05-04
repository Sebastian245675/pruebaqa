package com.qalab.qawebpractice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.qalab.qawebpractice.repository.UserAccountRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QaWebPracticeApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserAccountRepository userAccountRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void homePageLoads() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Aplicacion web para practicar QA")));
	}

	@Test
	void dashboardRequiresAuthentication() throws Exception {
		mockMvc.perform(get("/dashboard"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login"));
	}

	@Test
	void userCanRegisterFromPublicForm() throws Exception {
		String email = "nuevo.usuario@testlab.local";
		userAccountRepository.findByEmailIgnoreCase(email).ifPresent(userAccountRepository::delete);

		mockMvc.perform(post("/register")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.param("fullName", "Nuevo Usuario")
				.param("email", email)
				.param("password", "Password123!")
				.param("confirmPassword", "Password123!"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login?registered"));

		assertTrue(userAccountRepository.findByEmailIgnoreCase(email).isPresent());
	}

}
