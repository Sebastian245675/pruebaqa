package com.qalab.qawebpractice.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class WorkExperienceForm {

	@NotBlank(message = "El cargo es obligatorio.")
	@Size(max = 100, message = "El cargo no puede superar 100 caracteres.")
	private String jobTitle;

	@NotBlank(message = "La empresa es obligatoria.")
	@Size(max = 100, message = "La empresa no puede superar 100 caracteres.")
	private String company;

	@Size(max = 60, message = "El tipo de empleo no puede superar 60 caracteres.")
	private String employmentType;

	@Size(max = 80, message = "La ubicacion no puede superar 80 caracteres.")
	private String location;

	@NotNull(message = "La fecha de inicio es obligatoria.")
	private LocalDate startDate;

	private LocalDate endDate;

	private boolean currentPosition;

	@Size(max = 1200, message = "La descripcion no puede superar 1200 caracteres.")
	private String description;

	@AssertTrue(message = "La fecha final no puede ser anterior a la fecha de inicio.")
	public boolean isDateRangeValid() {
		if (startDate == null || endDate == null) {
			return true;
		}
		return !endDate.isBefore(startDate);
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public boolean isCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(boolean currentPosition) {
		this.currentPosition = currentPosition;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
