package com.trackmyjob.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response returned by the API for all error scenarios.
 *
 * @JsonInclude(NON_NULL) — fields with null values are excluded from
 * the JSON output. So if there are no field-level validation errors,
 * the "errors" key won't appear in the response at all.
 * Clean, minimal error responses.
 *
 * Example for validation error:
 * {
 *   "status": 400,
 *   "message": "Validation failed",
 *   "errors": { "email": "must be a valid email" },
 *   "timestamp": "2026-09-10T12:00:00"
 * }
 *
 * Example for not found error:
 * {
 *   "status": 404,
 *   "message": "Application not found",
 *   "timestamp": "2026-09-10T12:00:00"
 * }
 * (no "errors" field — it's null, so JsonInclude excludes it)
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

	private int status;
	private String message;
	private Map<String, String> errors;     // field-level validation errors
	private LocalDateTime timestamp;
}







