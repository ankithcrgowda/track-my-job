package com.trackmyjob.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when login credentials are wrong.
 * HTTP 401 Unauthorized — identity could not be verified.
 *
 * Note: we use the same generic message for both
 * "email not found" and "wrong password" cases.
 * Never tell the client WHICH part was wrong —
 * that leaks information useful to attackers.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends RuntimeException{

	public InvalidCredentialsException() {

		super("Invalid email or password");
	}
}
