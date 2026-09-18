package com.trackmyjob.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for user registration requests.
 *
 * @NotBlank  — field must not be null, empty, or whitespace only
 * @Email     — must be a valid email format (basic RFC check)
 * @Size      — enforces min/max length BEFORE it hits the database
 *
 * Why validate here AND in the DB?
 * DB constraints are the last line of defence.
 * Validating in the DTO gives us friendly error messages to send back
 * to the client. DB errors are ugly and leak internal details.
 */
@Data
public class RegisterRequest {

	@NotBlank(message = "name is required")
	@Size(min = 2, max = 100, message = "Name must be between 2 and maximum 100 characters")
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Please provide a valid email address")
	@Size(max = 150, message = "Email must not exceed 150 characters")
	private String email;

	@NotBlank(message = "name is required")
	@Size(min = 6, max = 100, message = "Password must be  between 6 to 100 characters")
	private String password;
}










