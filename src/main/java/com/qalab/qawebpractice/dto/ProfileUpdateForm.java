package com.qalab.qawebpractice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProfileUpdateForm {

	@NotBlank(message = "El nombre completo es obligatorio.")
	@Size(max = 80, message = "El nombre no puede superar 80 caracteres.")
	private String fullName;

	@Size(max = 80, message = "El cargo no puede superar 80 caracteres.")
	private String professionalTitle;

	@Size(max = 30, message = "El telefono no puede superar 30 caracteres.")
	private String phoneNumber;

	@Size(max = 100, message = "La ubicacion no puede superar 100 caracteres.")
	private String location;

	@Size(max = 1200, message = "La biografia no puede superar 1200 caracteres.")
	private String bio;

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getProfessionalTitle() {
		return professionalTitle;
	}

	public void setProfessionalTitle(String professionalTitle) {
		this.professionalTitle = professionalTitle;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}
}
