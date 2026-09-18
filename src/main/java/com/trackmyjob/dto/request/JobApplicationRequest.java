package com.trackmyjob.dto.request;


import com.trackmyjob.entity.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class JobApplicationRequest {

	@NotBlank(message = "Company name is required")
	@Size(max = 150, message = "Company name must not exceed 150 characters")
	private String companyName;

	@NotBlank(message = "Job role is required")
	@Size(max = 150, message = "Job role must not exceed 150 characters")
	private String jobRole;

	@NotNull(message = "Status is required")
	private ApplicationStatus status;

	@NotNull(message = "Applied date is required")
	private LocalDate appliedDate;

	// Optional - Not Blank
	private String description;
}
