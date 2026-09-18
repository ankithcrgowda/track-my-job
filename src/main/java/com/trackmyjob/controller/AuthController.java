package com.trackmyjob.controller;

import com.trackmyjob.dto.request.LoginRequest;
import com.trackmyjob.dto.request.RegisterRequest;
import com.trackmyjob.dto.response.AuthResponse;
import com.trackmyjob.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @RestController = @Controller + @ResponseBody
 * Means: every method returns data (JSON), not an HTML view.
 *
 * @RequestMapping("/api/auth") — all endpoints in this class
 * are prefixed with /api/auth
 *
 * The controller's ONLY job:
 * 1. Receive the HTTP request
 * 2. Pass data to the Service
 * 3. Return the HTTP response
 * No business logic. No database calls. Ever.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
/**
 * @Tag — groups endpoints under a named section in Swagger UI.
 * All methods in this controller appear under "Authentication".
 */
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

	private final AuthService authService;

	/**
	 * POST /api/auth/register
	 *
	 * @Valid — triggers validation annotations on RegisterRequest
	 *          (@NotBlank, @Email, @Size etc.)
	 *          If validation fails, Spring returns 400 Bad Request
	 *          automatically with error details. Phase 6 customizes this.
	 *
	 * ResponseEntity<AuthResponse> — lets us control the HTTP status code.
	 * 201 Created is the correct status for a successful registration.
	 * (200 OK is for reads, 201 for creates — a subtle but correct distinction)
	 */
	@PostMapping("/register")
	@Operation(
			summary = "Register a new User",
			description = "Creates a new user account and returns a JWT token",
			security = @SecurityRequirement(name = "") // overrides global — no auth needed
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "201",
					description = "User registered successfully",
					content = @Content(schema = @Schema(implementation = AuthResponse.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "Validation failed - Invalid input"
			),
			@ApiResponse(
					responseCode = "409",
					description = "Email already registered"
			)
	})
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		AuthResponse response = authService.register(request);
		return ResponseEntity.status((HttpStatus.CREATED)).body(response);
	}

	/**
	 * POST /api/auth/login
	 * 200 OK is correct for login — we're not creating a resource.
	 */
	@PostMapping("/login")
	@Operation(
			summary = "Login with existing credentials",
			description = "Authenticates user and returns a JWT token valid for 24 hours",
			security = @SecurityRequirement(name = "") // no auth needed
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Login Successful",
					content = @Content(schema = @Schema(implementation = AuthResponse.class))
			),
			@ApiResponse(
					responseCode = "401",
					description = "Invalid email or password!"
			)
	})
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		AuthResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}
}









