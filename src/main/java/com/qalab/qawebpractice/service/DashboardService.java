package com.qalab.qawebpractice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.qalab.qawebpractice.dto.ProfileUpdateForm;
import com.qalab.qawebpractice.dto.WorkExperienceForm;
import com.qalab.qawebpractice.model.UserAccount;
import com.qalab.qawebpractice.model.WorkExperience;
import com.qalab.qawebpractice.repository.UserAccountRepository;
import com.qalab.qawebpractice.repository.WorkExperienceRepository;

@Service
public class DashboardService {

	private final UserAccountRepository userAccountRepository;
	private final WorkExperienceRepository workExperienceRepository;

	public DashboardService(
		UserAccountRepository userAccountRepository,
		WorkExperienceRepository workExperienceRepository
	) {
		this.userAccountRepository = userAccountRepository;
		this.workExperienceRepository = workExperienceRepository;
	}

	@Transactional(readOnly = true)
	public UserAccount findUserByEmail(String email) {
		return userAccountRepository.findByEmailIgnoreCase(email)
			.orElseThrow(() -> new IllegalArgumentException("No se encontro el usuario autenticado."));
	}

	@Transactional(readOnly = true)
	public List<WorkExperience> findExperiencesForUser(Long userId) {
		return workExperienceRepository.findByUserAccountIdOrderByCurrentPositionDescStartDateDescCreatedAtDesc(userId);
	}

	@Transactional
	public UserAccount updateProfile(String email, ProfileUpdateForm form) {
		UserAccount userAccount = findUserByEmail(email);
		userAccount.setFullName(form.getFullName().trim());
		userAccount.setProfessionalTitle(trimToNull(form.getProfessionalTitle()));
		userAccount.setPhoneNumber(trimToNull(form.getPhoneNumber()));
		userAccount.setLocation(trimToNull(form.getLocation()));
		userAccount.setBio(trimToNull(form.getBio()));
		return userAccountRepository.save(userAccount);
	}

	@Transactional
	public WorkExperience addExperience(String email, WorkExperienceForm form) {
		UserAccount userAccount = findUserByEmail(email);

		WorkExperience workExperience = new WorkExperience();
		workExperience.setUserAccount(userAccount);
		workExperience.setJobTitle(form.getJobTitle().trim());
		workExperience.setCompany(form.getCompany().trim());
		workExperience.setEmploymentType(trimToNull(form.getEmploymentType()));
		workExperience.setLocation(trimToNull(form.getLocation()));
		workExperience.setStartDate(form.getStartDate());
		workExperience.setCurrentPosition(form.isCurrentPosition());
		workExperience.setEndDate(form.isCurrentPosition() ? null : form.getEndDate());
		workExperience.setDescription(trimToNull(form.getDescription()));

		return workExperienceRepository.save(workExperience);
	}

	@Transactional
	public void deleteExperience(String email, Long experienceId) {
		WorkExperience workExperience = workExperienceRepository.findByIdAndUserAccountEmailIgnoreCase(experienceId, email)
			.orElseThrow(() -> new IllegalArgumentException("No se encontro la experiencia solicitada."));
		workExperienceRepository.delete(workExperience);
	}

	public int calculateProfileCompletion(UserAccount userAccount) {
		int completedFields = 0;
		if (StringUtils.hasText(userAccount.getFullName())) {
			completedFields++;
		}
		if (StringUtils.hasText(userAccount.getProfessionalTitle())) {
			completedFields++;
		}
		if (StringUtils.hasText(userAccount.getPhoneNumber())) {
			completedFields++;
		}
		if (StringUtils.hasText(userAccount.getLocation())) {
			completedFields++;
		}
		if (StringUtils.hasText(userAccount.getBio())) {
			completedFields++;
		}
		return completedFields * 20;
	}

	private String trimToNull(String value) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		return value.trim();
	}
}
