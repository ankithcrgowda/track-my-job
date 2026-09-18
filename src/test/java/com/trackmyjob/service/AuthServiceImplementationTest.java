package com.trackmyjob.service;

import com.trackmyjob.dto.request.LoginRequest;
import com.trackmyjob.dto.request.RegisterRequest;
import com.trackmyjob.dto.response.AuthResponse;
import com.trackmyjob.entity.User;
import com.trackmyjob.exception.EmailAlreadyExistsException;
import com.trackmyjob.exception.InvalidCredentialsException;
import com.trackmyjob.repository.UserRepository;
import com.trackmyjob.security.JwtUtil;
import com.trackmyjob.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthServiceImpl.
 *
 * @ExtendWith(MockitoExtension.class) — activates Mockito for this test.
 * No Spring context is loaded. No database. Pure Java.
 * This makes tests run in milliseconds.
 *
 * @Mock — creates a fake (mock) version of the dependency.
 * We control exactly what it returns in each test.
 *
 * @InjectMocks — creates a real AuthServiceImpl instance and
 * injects all @Mock fields into it automatically.
 *
 * WHY MOCK?
 * We're testing AuthServiceImpl's LOGIC, not UserRepository's logic.
 * We tell the mock "when findByEmail is called, return this user"
 * so we can focus purely on what AuthServiceImpl does with that user.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceImplementationTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private AuthServiceImpl authService;

	private RegisterRequest registerRequest;
	private LoginRequest loginRequest;
	private User savedUser;

	/**
	 * @BeforeEach — runs before EVERY test method.
	 * Sets up fresh test data so tests don't share state.
	 */
	@BeforeEach
	void setUp() {
		registerRequest = new RegisterRequest();
		registerRequest.setName("Ankith CR");
		registerRequest.setEmail("ankith@example.com");
		registerRequest.setPassword("password123");

		loginRequest = new LoginRequest();
		loginRequest.setEmail("ankith@example.com");
		loginRequest.setPassword("password123");

		savedUser = User.builder()
				.name("Ankith CR")
				.email("ankith@example.com")
				.password("$2a$10$hashedpassword")
				.build();
	}

	// ─── REGISTER TESTS ───────────────────────────────────────────

	@Test
	@DisplayName("Register — success returns AuthResponse with token")
	void register_Success_ReturnsAuthResponseWithToken() {
		// ARRANGE — set up mock behaviour
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedpassword");
		when(userRepository.save(any(User.class))).thenReturn(savedUser);
		when(jwtUtil.generateToken(anyString())).thenReturn("mock.jwt.token");

		// ACT — call the method we're testing
		AuthResponse response = authService.register(registerRequest);

		// ASSERT — verify the response
		assertThat(response).isNotNull();
		assertThat(response.getEmail()).isEqualTo("ankith@example.com");
		assertThat(response.getName()).isEqualTo("Ankith CR");
		assertThat(response.getToken()).isEqualTo("mock.jwt.token");
		assertThat(response.getMessage()).isEqualTo("Registration Successful");

		// VERIFY — confirm the right methods were called
		verify(userRepository).existsByEmail("ankith@example.com");
		verify(passwordEncoder).encode("password123");
		verify(userRepository).save(any(User.class));
		verify(jwtUtil).generateToken("ankith@example.com");
	}

	@Test
	@DisplayName("Register — duplicate email throws EmailAlreadyExistsException")
	void register_DuplicateEmail_ThrowsEmailAlreadyExistsException() {
		// ARRANGE — email already exists
		when(userRepository.existsByEmail(anyString())).thenReturn(true);

		// ACT & ASSERT — expect the exception
		assertThatThrownBy(() -> authService.register(registerRequest))
				.isInstanceOf(EmailAlreadyExistsException.class)
				.hasMessageContaining("ankith@example.com");

		// VERIFY — save should never be called when email exists
		verify(userRepository, never()).save(any(User.class));
	}

	// ─── LOGIN TESTS ──────────────────────────────────────────────

	@Test
	@DisplayName("Login — success returns AuthResponse with token")
	void login_Success_ReturnsAuthResponseWithToken() {
		// ARRANGE
		when(userRepository.findByEmail(anyString()))
				.thenReturn(Optional.of(savedUser));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(jwtUtil.generateToken(anyString())).thenReturn("mock.jwt.token");

		// ACT
		AuthResponse response = authService.login(loginRequest);

		// ASSERT
		assertThat(response).isNotNull();
		assertThat(response.getMessage()).isEqualTo("Login Successful");
		assertThat(response.getToken()).isEqualTo("mock.jwt.token");
		assertThat(response.getEmail()).isEqualTo("ankith@example.com");
	}

	@Test
	@DisplayName("Login — wrong email throws InvalidCredentialsException")
	void login_WrongEmail_ThrowsInvalidCredentialsException() {
		// ARRANGE — user not found
		when(userRepository.findByEmail(anyString()))
				.thenReturn(Optional.empty());

		// ACT & ASSERT
		assertThatThrownBy(() -> authService.login(loginRequest))
				.isInstanceOf(InvalidCredentialsException.class)
				.hasMessage("Invalid email or password");
	}

	@Test
	@DisplayName("Login — wrong password throws InvalidCredentialsException")
	void login_WrongPassword_ThrowsInvalidCredentialsException() {
		// ARRANGE — user found but password doesn't match
		when(userRepository.findByEmail(anyString()))
				.thenReturn(Optional.of(savedUser));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

		// ACT & ASSERT
		assertThatThrownBy(() -> authService.login(loginRequest))
				.isInstanceOf(InvalidCredentialsException.class)
				.hasMessage("Invalid email or password");

		// VERIFY — token should never be generated for failed login
		verify(jwtUtil, never()).generateToken(anyString());
	}
}