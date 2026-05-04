package com.qalab.qawebpractice.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.qalab.qawebpractice.dto.ProfileUpdateForm;
import com.qalab.qawebpractice.dto.WorkExperienceForm;
import com.qalab.qawebpractice.model.UserAccount;
import com.qalab.qawebpractice.model.WorkExperience;
import com.qalab.qawebpractice.service.DashboardService;

import jakarta.validation.Valid;

@Controller
public class HomeController {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	private final DashboardService dashboardService;

	public HomeController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@GetMapping("/dashboard")
	public String dashboard(Authentication authentication, Model model) {
		UserAccount userAccount = dashboardService.findUserByEmail(authentication.getName());
		populateDashboardModel(model, userAccount, createProfileForm(userAccount), new WorkExperienceForm());

		return "dashboard";
	}

	@PostMapping("/dashboard/profile")
	public String updateProfile(
		Authentication authentication,
		@Valid @ModelAttribute("profileForm") ProfileUpdateForm profileForm,
		BindingResult bindingResult,
		Model model
	) {
		UserAccount userAccount = dashboardService.findUserByEmail(authentication.getName());
		if (bindingResult.hasErrors()) {
			populateDashboardModel(model, userAccount, profileForm, new WorkExperienceForm());
			model.addAttribute("profileFormHasErrors", true);
			return "dashboard";
		}

		dashboardService.updateProfile(authentication.getName(), profileForm);
		return "redirect:/dashboard?profileUpdated";
	}

	@PostMapping("/dashboard/experiences")
	public String addExperience(
		Authentication authentication,
		@Valid @ModelAttribute("experienceForm") WorkExperienceForm experienceForm,
		BindingResult bindingResult,
		Model model
	) {
		UserAccount userAccount = dashboardService.findUserByEmail(authentication.getName());
		if (bindingResult.hasErrors()) {
			populateDashboardModel(model, userAccount, createProfileForm(userAccount), experienceForm);
			model.addAttribute("experienceFormHasErrors", true);
			return "dashboard";
		}

		dashboardService.addExperience(authentication.getName(), experienceForm);
		return "redirect:/dashboard?experienceAdded";
	}

	@PostMapping("/dashboard/experiences/{experienceId}/delete")
	public String deleteExperience(Authentication authentication, @PathVariable Long experienceId) {
		dashboardService.deleteExperience(authentication.getName(), experienceId);
		return "redirect:/dashboard?experienceDeleted";
	}

	private void populateDashboardModel(
		Model model,
		UserAccount userAccount,
		ProfileUpdateForm profileForm,
		WorkExperienceForm experienceForm
	) {
		List<WorkExperience> experiences = dashboardService.findExperiencesForUser(userAccount.getId());

		model.addAttribute("user", userAccount);
		model.addAttribute("memberSince", userAccount.getCreatedAt().format(DATE_TIME_FORMATTER));
		model.addAttribute("currentMoment", LocalDateTime.now().format(DATE_TIME_FORMATTER));
		model.addAttribute("profileForm", profileForm);
		model.addAttribute("experienceForm", experienceForm);
		model.addAttribute("experiences", experiences);
		model.addAttribute("experiencesCount", experiences.size());
		model.addAttribute("profileCompletion", dashboardService.calculateProfileCompletion(userAccount));
	}

	private ProfileUpdateForm createProfileForm(UserAccount userAccount) {
		ProfileUpdateForm profileForm = new ProfileUpdateForm();
		profileForm.setFullName(userAccount.getFullName());
		profileForm.setProfessionalTitle(userAccount.getProfessionalTitle());
		profileForm.setPhoneNumber(userAccount.getPhoneNumber());
		profileForm.setLocation(userAccount.getLocation());
		profileForm.setBio(userAccount.getBio());
		return profileForm;
	}
}
