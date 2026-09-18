package com.trackmyjob.service;

import com.trackmyjob.dto.request.LoginRequest;
import com.trackmyjob.dto.request.RegisterRequest;
import com.trackmyjob.dto.response.AuthResponse;

/**
 * Defines the authentication contract.
 *
 * Why an interface + separate implementation?
 * 1. Testability — in unit tests, we can mock this interface
 * 2. Flexibility — swap implementations without changing callers
 * 3. Convention — standard Spring Boot best practice
 *
 * The Controller depends on THIS interface, not the implementation.
 * This is the Dependency Inversion Principle (the D in SOLID).
 */
public interface AuthService {

	AuthResponse register(RegisterRequest request);
	AuthResponse login(LoginRequest request);
}
