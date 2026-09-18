package com.trackmyjob.service.impl;

import com.trackmyjob.dto.request.LoginRequest;
import com.trackmyjob.dto.request.RegisterRequest;
import com.trackmyjob.dto.response.AuthResponse;
import com.trackmyjob.entity.User;
import com.trackmyjob.exception.EmailAlreadyExistsException;
import com.trackmyjob.exception.InvalidCredentialsException;
import com.trackmyjob.repository.UserRepository;
import com.trackmyjob.security.JwtUtil;
import com.trackmyjob.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @Service — marks this as a Spring-managed service bean.
 * Spring creates one instance of this class and injects it
 * wherever AuthService is needed (e.g. AuthController).
 *
 * @RequiredArgsConstructor (Lombok) — generates a constructor
 * for all 'final' fields. Spring sees this constructor and
 * injects the dependencies automatically.
 *
 * This is CONSTRUCTOR INJECTION — the recommended way to inject
 * dependencies in Spring. Never use @Autowired on fields.
 * Why? Constructor injection makes dependencies explicit and
 * makes the class easier to unit test.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Override
	public AuthResponse register(RegisterRequest request) {

		// Step 1 — Check if email is already registered
		// Before: throw new RuntimeException("Email already registered...")
		// After: specific exception with correct HTTP status
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException(request.getEmail());
		}

		// Step 2 — Build the User entity from the request DTO
		// Notice: we NEVER store the plain text password
		// BCrypt turns "mypassword123" into "$2a$10$..." (60-char hash)
		User user = User.builder()
				.name(request.getName())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.build();

		// Step 3 — Save to database
		// Spring Data JPA calls Hibernate which runs INSERT INTO users (...)
		User addUser = userRepository.save(user);

		// Generate JWT immediately after registratio
		String token = jwtUtil.generateToken(addUser.getEmail());

		return AuthResponse.builder()
				.message("Registration Successful")
				.email(addUser.getEmail())
				.name(addUser.getName())
				.token(token)
				.build();
	}

	@Override
	public AuthResponse login(LoginRequest request) {

		// Step 1 — Find user by email
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(InvalidCredentialsException::new);
		// Note: we say "Invalid email OR password" — never tell the client
		// which one is wrong. That leaks information to attackers.

		// Step 2 — Verify the password
		// BCrypt.matches() hashes the incoming password and compares
		// it to the stored hash. We NEVER decrypt — BCrypt is one-way.
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException();
		}

		// Generate JWT on successful login
		String token = jwtUtil.generateToken(user.getEmail());

		return AuthResponse.builder()
				.message("Login Successful")
				.email(user.getEmail())
				.name(user.getName())
				.token(token)
				.build();
	}
}








