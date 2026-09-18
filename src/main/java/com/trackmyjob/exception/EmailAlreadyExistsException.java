package com.trackmyjob.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a registration attempt uses an email already in the DB.
 * HTTP 409 Conflict — the request conflicts with existing data.
 *
 * Why 409 and not 400?
 * 400 Bad Request = the request itself is malformed.
 * 409 Conflict = the request is valid but conflicts with current state.
 * The email format is fine — it just already exists. That's a 409.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException{

	public EmailAlreadyExistsException(String message) {
		super(message);
	}
}
