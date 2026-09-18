package com.trackmyjob.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested resource doesn't exist.
 * Examples: job application not found, user not found.
 *
 * @ResponseStatus — hints to Spring what HTTP status this maps to.
 * Our GlobalExceptionHandler reads this and returns 404.
 *
 * Extends RuntimeException — unchecked, so we don't need
 * to declare it in method signatures (cleaner code).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
