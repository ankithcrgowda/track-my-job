package com.trackmyjob.exception;

import com.trackmyjob.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the entire application.
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 * It intercepts exceptions thrown by ANY @RestController
 * and converts them to structured JSON error responses.
 *
 * Think of it as a safety net — any exception that escapes
 * a controller lands here instead of propagating as a 500.
 *
 * How it works:
 * When an exception is thrown anywhere in the app, Spring
 * scans this class for a matching @ExceptionHandler method.
 * The most specific matching handler wins.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Handles @Valid validation failures.
	 *
	 * When a request body fails validation (e.g. blank email,
	 * short password), Spring throws MethodArgumentNotValidException
	 * before the controller method even runs.
	 *
	 * We extract all field errors and put them in a Map:
	 * { "email": "must be a valid email", "password": "too short" }
	 *
	 * HTTP 400 Bad Request — the client sent invalid data.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

		Map<String, String> fieldErrors = new HashMap<>();
		// getBindingResult() contains all validation failures
		// Each FieldError has: field name + rejection reason
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			fieldErrors.put(fieldName, errorMessage);
		});

		ErrorResponse errorResponse = ErrorResponse.builder()
				.status(HttpStatus.BAD_REQUEST.value())
				.message("Validation failed")
				.errors(fieldErrors)
				.timestamp(LocalDateTime.now())
				.build();

		return ResponseEntity.badRequest().body(errorResponse);
	}

	/**
	 * Handles ResourceNotFoundException → 404 Not Found.
	 * Thrown when: job application not found, user not found.
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
			ResourceNotFoundException ex) {

		ErrorResponse errorResponse = ErrorResponse.builder()
				.status(HttpStatus.NOT_FOUND.value())
				.message(ex.getMessage())
				.timestamp(LocalDateTime.now())
				.build();

		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(errorResponse);
	}

	/**
	 * Handles EmailAlreadyExistsException → 409 Conflict.
	 * Thrown when: registration with an already-used email.
	 */
	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(
			EmailAlreadyExistsException ex) {

		ErrorResponse errorResponse = ErrorResponse.builder()
				.status(HttpStatus.CONFLICT.value())
				.message(ex.getMessage())
				.timestamp(LocalDateTime.now())
				.build();

		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(errorResponse);
	}

	/**
	 * Handles InvalidCredentialsException → 401 Unauthorized.
	 * Thrown when: wrong email or password during login.
	 */
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
			InvalidCredentialsException ex) {

		ErrorResponse errorResponse = ErrorResponse.builder()
				.status(HttpStatus.UNAUTHORIZED.value())
				.message(ex.getMessage())
				.timestamp(LocalDateTime.now())
				.build();

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(errorResponse);
	}

	/**
	 * Catch-all handler — last resort for any unhandled exception.
	 *
	 * Without this, unexpected exceptions return Spring's default
	 * ugly error response. This ensures EVERY error has our
	 * consistent format, even ones we didn't anticipate.
	 *
	 * HTTP 500 Internal Server Error.
	 *
	 * Note: we return a generic message to the client.
	 * The real exception details should be logged (Phase 8).
	 * Never expose internal stack traces in API responses.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(
			Exception ex) {

		ex.printStackTrace();
		ErrorResponse errorResponse = ErrorResponse.builder()
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.message("An unexpected error occurred. Please try again.")
				.timestamp(LocalDateTime.now())
				.build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(errorResponse);
	}
}
