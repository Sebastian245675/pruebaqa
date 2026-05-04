package com.qalab.qawebpractice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qalab.qawebpractice.model.UserAccount;
import com.qalab.qawebpractice.model.WorkExperience;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {

	List<WorkExperience> findByUserAccountIdOrderByCurrentPositionDescStartDateDescCreatedAtDesc(Long userId);

	Optional<WorkExperience> findByIdAndUserAccountEmailIgnoreCase(Long id, String email);

	boolean existsByUserAccountAndCompanyAndJobTitleAndStartDate(
		UserAccount userAccount,
		String company,
		String jobTitle,
		LocalDate startDate
	);
}
