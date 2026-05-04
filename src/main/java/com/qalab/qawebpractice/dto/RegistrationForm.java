package com.qalab.qawebpractice.dto;

import org.springframework.util.StringUtils;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

	@NotBlank(message = "El nombre es obligatorio.")
	@Size(max = 80, message = "El nombre no puede superar 80 caracteres.")
	private String fullName;

	@NotBlank(message = "El correo es obligatorio.")
	@Email(message = "Ingresa un correo valido.")
	@Size(max = 120, message = "El correo no puede superar 120 caracteres.")
	private String email;

	@NotBlank(message = "La contrasena es obligatoria.")
	@Size(min = 6, max = 40, message = "La contrasena debe tener entre 6 y 40 caracteres.")
	private String password;

	@NotBlank(message = "Debes confirmar la contrasena.")
	private String confirmPassword;

	@AssertTrue(message = "Las contrasenas no coinciden.")
	public boolean isPasswordConfirmationValid() {
		if (!StringUtils.hasText(password) || !StringUtils.hasText(confirmPassword)) {
			return true;
		}
		return password.equals(confirmPassword);
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
}
