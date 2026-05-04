package com.qalab.qawebpractice;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.qalab.qawebpractice.repository.UserAccountRepository;
import com.qalab.qawebpractice.repository.WorkExperienceRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QAnuevoscampposeducacion {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserAccountRepository userAccountRepository;

	@Autowired
	private WorkExperienceRepository workExperienceRepository;

	@Test
	void authenticatedUserCanUpdateProfile() throws Exception {
		mockMvc.perform(post("/dashboard/profile")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(SecurityMockMvcRequestPostProcessors.user("qa.user@testlab.local").roles("USER"))
				.param("fullName", "QA User Updated")
				.param("professionalTitle", "Automation QA Engineer")
				.param("phoneNumber", "+57 311 555 1111")
				.param("location", "Cali, Colombia")
				.param("bio", "Perfil actualizado desde una prueba automatizada."))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/dashboard?profileUpdated"));

		var user = userAccountRepository.findByEmailIgnoreCase("qa.user@testlab.local").orElseThrow();
		assertEquals("QA User Updated", user.getFullName());
		assertEquals("Automation QA Engineer", user.getProfessionalTitle());
		assertEquals("Cali, Colombia", user.getLocation());
	}

	@Test
	void authenticatedUserCanAddExperience() throws Exception {
		Long userId = userAccountRepository.findByEmailIgnoreCase("qa.user@testlab.local").orElseThrow().getId();
		long experiencesBefore = workExperienceRepository.findByUserAccountIdOrderByCurrentPositionDescStartDateDescCreatedAtDesc(userId)
			.size();

		mockMvc.perform(post("/dashboard/experiences")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(SecurityMockMvcRequestPostProcessors.user("qa.user@testlab.local").roles("USER"))
				.param("jobTitle", "QA Specialist")
				.param("company", "Demo Company")
				.param("employmentType", "Contrato")
				.param("location", "Remoto")
				.param("startDate", LocalDate.of(2026, 1, 10).toString())
				.param("endDate", LocalDate.of(2026, 4, 10).toString())
				.param("description", "Nueva experiencia creada desde MockMvc."))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/dashboard?experienceAdded"));

		long experiencesAfter = workExperienceRepository.findByUserAccountIdOrderByCurrentPositionDescStartDateDescCreatedAtDesc(userId)
			.size();
		assertTrue(experiencesAfter > experiencesBefore);
	}

	@Test
	void authenticatedUserCanDeleteExperience() throws Exception {
		var user = userAccountRepository.findByEmailIgnoreCase("qa.user@testlab.local").orElseThrow();
		var experience = workExperienceRepository.findByUserAccountIdOrderByCurrentPositionDescStartDateDescCreatedAtDesc(user.getId())
			.stream()
			.findFirst()
			.orElseThrow();

		mockMvc.perform(post("/dashboard/experiences/" + experience.getId() + "/delete")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(SecurityMockMvcRequestPostProcessors.user("qa.user@testlab.local").roles("USER")))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/dashboard?experienceDeleted"));

		assertTrue(workExperienceRepository.findById(experience.getId()).isEmpty());
	}

	@Test
	void experienceFormShowsErrorWhenEndDateIsBeforeStartDate() throws Exception {
		mockMvc.perform(post("/dashboard/experiences")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(SecurityMockMvcRequestPostProcessors.user("qa.user@testlab.local").roles("USER"))
				.param("jobTitle", "QA Specialist")
				.param("company", "Fecha Invalida Corp")
				.param("employmentType", "Contrato")
				.param("location", "Remoto")
				.param("startDate", LocalDate.of(2026, 5, 10).toString())
				.param("endDate", LocalDate.of(2026, 4, 10).toString())
				.param("description", "Caso negativo para validar fechas."))
			.andExpect(status().isOk())
			.andExpect(content().string(org.hamcrest.Matchers.containsString(
				"La fecha final no puede ser anterior a la fecha de inicio."
			)));
	}
}
