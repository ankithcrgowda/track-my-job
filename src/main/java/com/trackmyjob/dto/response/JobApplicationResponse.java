package com.trackmyjob.dto.response;

import com.trackmyjob.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Returned when fetching job application data.
 *
 * Notice: no 'user' field here.
 * We never expose the full User object in a response —
 * that would leak password hashes and other sensitive data.
 * We only expose what the client actually needs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationResponse {

	private Long id;
	private String companyName;
	private String jobRole;
	private ApplicationStatus status;
	private LocalDate appliedDate;
	private String description;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
