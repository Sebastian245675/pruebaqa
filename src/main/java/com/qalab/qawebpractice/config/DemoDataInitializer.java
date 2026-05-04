package com.qalab.qawebpractice.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.qalab.qawebpractice.model.UserAccount;
import com.qalab.qawebpractice.model.WorkExperience;
import com.qalab.qawebpractice.repository.UserAccountRepository;
import com.qalab.qawebpractice.repository.WorkExperienceRepository;

@Configuration
public class DemoDataInitializer {

	@Bean
	CommandLineRunner seedUsers(
		UserAccountRepository userAccountRepository,
		WorkExperienceRepository workExperienceRepository,
		PasswordEncoder passwordEncoder
	) {
		return args -> {
			UserAccount adminUser = createUserIfMissing(
				userAccountRepository,
				passwordEncoder,
				"QA Admin",
				"qa.admin@testlab.local",
				"Admin123!",
				"ADMIN",
				"Lead QA Engineer",
				"Bogota, Colombia",
				"+57 300 000 0001",
				"Admin demo para validar roles, sesiones y mantenimiento del perfil."
			);
			UserAccount qaUser = createUserIfMissing(
				userAccountRepository,
				passwordEncoder,
				"QA Learner",
				"qa.user@testlab.local",
				"User123!",
				"USER",
				"Junior QA Analyst",
				"Medellin, Colombia",
				"+57 300 000 0002",
				"Usuario demo para practicar registro, login, actualizacion de perfil y formularios."
			);

			createExperienceIfMissing(
				workExperienceRepository,
				adminUser,
				"Lead QA Engineer",
				"TestLab Corp",
				LocalDate.of(2023, 1, 1),
				null,
				true,
				"Tiempo completo",
				"Bogota",
				"Lidera estrategia de automatizacion, regression y pruebas de seguridad."
			);
			createExperienceIfMissing(
				workExperienceRepository,
				qaUser,
				"QA Intern",
				"SoftAcademy",
				LocalDate.of(2024, 7, 1),
				LocalDate.of(2025, 2, 28),
				false,
				"Internship",
				"Remoto",
				"Apoyo en casos de prueba manuales, reportes de bugs y smoke testing."
			);
			createExperienceIfMissing(
				workExperienceRepository,
				qaUser,
				"Junior QA Analyst",
				"Bright Apps",
				LocalDate.of(2025, 3, 1),
				null,
				true,
				"Tiempo completo",
				"Medellin",
				"Automatiza flujos de login, dashboard y formularios con Selenium."
			);
		};
	}

	private UserAccount createUserIfMissing(
		UserAccountRepository userAccountRepository,
		PasswordEncoder passwordEncoder,
		String fullName,
		String email,
		String rawPassword,
		String role,
		String professionalTitle,
		String location,
		String phoneNumber,
		String bio
	) {
		return userAccountRepository.findByEmailIgnoreCase(email)
			.map(existingUser -> {
				existingUser.setProfessionalTitle(professionalTitle);
				existingUser.setLocation(location);
				existingUser.setPhoneNumber(phoneNumber);
				existingUser.setBio(bio);
				return userAccountRepository.save(existingUser);
			})
			.orElseGet(() -> {
				UserAccount userAccount = new UserAccount();
				userAccount.setFullName(fullName);
				userAccount.setEmail(email);
				userAccount.setPassword(passwordEncoder.encode(rawPassword));
				userAccount.setRole(role);
				userAccount.setProfessionalTitle(professionalTitle);
				userAccount.setLocation(location);
				userAccount.setPhoneNumber(phoneNumber);
				userAccount.setBio(bio);
				return userAccountRepository.save(userAccount);
			});
	}

	private void createExperienceIfMissing(
		WorkExperienceRepository workExperienceRepository,
		UserAccount userAccount,
		String jobTitle,
		String company,
		LocalDate startDate,
		LocalDate endDate,
		boolean currentPosition,
		String employmentType,
		String location,
		String description
	) {
		if (workExperienceRepository.existsByUserAccountAndCompanyAndJobTitleAndStartDate(
			userAccount,
			company,
			jobTitle,
			startDate
		)) {
			return;
		}

		WorkExperience workExperience = new WorkExperience();
		workExperience.setUserAccount(userAccount);
		workExperience.setJobTitle(jobTitle);
		workExperience.setCompany(company);
		workExperience.setStartDate(startDate);
		workExperience.setEndDate(endDate);
		workExperience.setCurrentPosition(currentPosition);
		workExperience.setEmploymentType(employmentType);
		workExperience.setLocation(location);
		workExperience.setDescription(description);
		workExperienceRepository.save(workExperience);
	}
}
