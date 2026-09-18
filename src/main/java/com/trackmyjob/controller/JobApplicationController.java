package com.trackmyjob.controller;

import com.trackmyjob.dto.request.JobApplicationRequest;
import com.trackmyjob.dto.response.JobApplicationResponse;
import com.trackmyjob.entity.JobApplication;
import com.trackmyjob.repository.UserRepository;
import com.trackmyjob.service.JobApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Job Application CRUD operations.
 *userId no longer comes from ?userId query param.
 * It's extracted from the JWT token via @AuthenticationPrincipal.
 *
 * @AuthenticationPrincipal — Spring injects the UserDetails object
 * that our JwtAuthFilter placed in the SecurityContext.
 * UserDetails.getUsername() returns the email.
 * We use the email to look up the userId from the DB.
 *
 * This is secure — the client cannot fake their userId anymore.
 * The token is cryptographically signed — tampering = rejection.
 */

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(
		name = "Job Applications",
		description = "CRUD operations for job applications - all require JWT"
)
public class JobApplicationController {

	private final JobApplicationService jobApplicationService;
	private final UserRepository userRepository;

	/**
	 * Helper: extract userId from the authenticated user's email.
	 * Called at the start of every endpoint method.
	 */
	private Long getCurrentUserId(UserDetails userDetails) {
		return userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"))
				.getId();
	}


	// POST /api/applications?userId=1
	@PostMapping
	@Operation(summary = "Create a job application",
			description = "Adds a new job application for the authenticated user")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Application created",
					content = @Content(schema = @Schema(
							implementation = JobApplicationResponse.class))),
			@ApiResponse(responseCode = "400", description = "Validation failed"),
			@ApiResponse(responseCode = "401", description = "JWT token missing or invalid")
	})
	public ResponseEntity<JobApplicationResponse> create(
			@AuthenticationPrincipal UserDetails userDetails,
			@Valid @RequestBody JobApplicationRequest request ) {

		Long userId = getCurrentUserId(userDetails);
		return ResponseEntity.status(HttpStatus.CREATED).body(jobApplicationService.create(userId, request));
	}

	// GET /api/applications?userId=1
	@GetMapping
	@Operation(summary = "Get all job applications",
			description = "Returns all job applications for the authenticated user")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "List returned successfully"),
			@ApiResponse(responseCode = "401", description = "JWT token missing or invalid")
	})
	public ResponseEntity<List<JobApplicationResponse>> getAll(
			@AuthenticationPrincipal UserDetails userDetails) {
		Long userId = getCurrentUserId(userDetails);
		return ResponseEntity.ok(jobApplicationService.getAllByUser(userId));
	}

	// GET /api/applications/3?userId=1
	@GetMapping("/{id}")
	@Operation(summary = "Get a job application by ID",
			description = "Returns a single application — only if it belongs to the authenticated user")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Application found"),
			@ApiResponse(responseCode = "401", description = "JWT token missing or invalid"),
			@ApiResponse(responseCode = "404", description = "Application not found")
	})
	public ResponseEntity<JobApplicationResponse> getById(
			@PathVariable Long id,
			@AuthenticationPrincipal UserDetails userDetails ) {
		Long userId = getCurrentUserId(userDetails);
		return ResponseEntity.ok(jobApplicationService.getById(userId, id));
	}

	// PUT /api/applications/3?userId=1
	@PutMapping("/{id}")
	@Operation(summary = "Update a job application",
			description = "Updates all fields of an existing application")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Application updated"),
			@ApiResponse(responseCode = "400", description = "Validation failed"),
			@ApiResponse(responseCode = "401", description = "JWT token missing or invalid"),
			@ApiResponse(responseCode = "404", description = "Application not found")
	})
	public ResponseEntity<JobApplicationResponse> update (
			@PathVariable Long id,
			@AuthenticationPrincipal UserDetails userDetails,
			@Valid @RequestBody JobApplicationRequest request ) {
		Long userId = getCurrentUserId(userDetails);
		return ResponseEntity.ok(jobApplicationService.update(userId, id, request));
	}

	// DELETE /api/applications/3?userId=1
	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a job application",
			description = "Permanently deletes an application — only if it belongs to the authenticated user")
	@ApiResponses({
			@ApiResponse(
					responseCode = "204",
					description = "Application deleted"
			),
			@ApiResponse(
					responseCode = "401",
					description = "JWT token missing or invalid"
			),
			@ApiResponse(
					responseCode = "404",
					description = "Application not found"
			)
	})
	public ResponseEntity<JobApplicationResponse> delete(
			@PathVariable Long id,
			@AuthenticationPrincipal UserDetails userDetails ) {
		jobApplicationService.delete(getCurrentUserId(userDetails), id);
		return ResponseEntity.noContent().build();
		// 204 No Content - correct HTTP status for successful DELETE
	}
}






