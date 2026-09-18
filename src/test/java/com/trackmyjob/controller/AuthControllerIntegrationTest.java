package com.trackmyjob.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trackmyjob.dto.request.LoginRequest;
import com.trackmyjob.dto.request.RegisterRequest;
import com.trackmyjob.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for AuthController.
 *
 * @SpringBootTest — loads the FULL Spring application context.
 * Real beans, real security config, real service layer.
 * Uses H2 in-memory DB (from application-test.properties).
 *
 * @AutoConfigureMockMvc — sets up MockMvc automatically.
 * MockMvc lets us send HTTP requests without starting a real server.
 * Faster than a real server but tests the full Spring MVC stack.
 *
 * @ActiveProfiles("test") — activates application-test.properties.
 * Uses H2 instead of PostgreSQL for tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("AuthController Integration Tests")
class AuthControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	/**
	 * Clean DB before each test.
	 * Integration tests share the same H2 instance —
	 * without cleanup, data from one test bleeds into the next.
	 */
	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
	}

	@Test
	@DisplayName("POST /api/auth/register — success returns 201 with token")
	void register_ValidRequest_Returns201WithToken() throws Exception {
		RegisterRequest request = new RegisterRequest();
		request.setName("Ankith CR");
		request.setEmail("ankith@example.com");
		request.setPassword("password123");

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.token", notNullValue()))
				.andExpect(jsonPath("$.email", is("ankith@example.com")))
				.andExpect(jsonPath("$.name", is("Ankith CR")))
				.andExpect(jsonPath("$.message", is("Registration Successful")));
	}

	@Test
	@DisplayName("POST /api/auth/register — duplicate email returns 409")
	void register_DuplicateEmail_Returns409() throws Exception {
		RegisterRequest request = new RegisterRequest();
		request.setName("Ankith CR");
		request.setEmail("ankith@example.com");
		request.setPassword("password123");

		// First registration
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());

		// Second registration with same email
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.status", is(409)))
				.andExpect(jsonPath("$.message",
						containsString("ankith@example.com")));
	}

	@Test
	@DisplayName("POST /api/auth/register — invalid input returns 400")
	void register_InvalidInput_Returns400WithFieldErrors() throws Exception {
		RegisterRequest request = new RegisterRequest();
		request.setName("");           // blank — fails @NotBlank
		request.setEmail("not-valid"); // invalid format — fails @Email
		request.setPassword("123");    // too short — fails @Size

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status", is(400)))
				.andExpect(jsonPath("$.message", is("Validation failed")))
				.andExpect(jsonPath("$.errors", notNullValue()))
				.andExpect(jsonPath("$.errors.email", notNullValue()))
				.andExpect(jsonPath("$.errors.password", notNullValue()));
	}

	@Test
	@DisplayName("POST /api/auth/login — success returns 200 with token")
	void login_ValidCredentials_Returns200WithToken() throws Exception {
		// Register first
		RegisterRequest registerRequest = new RegisterRequest();
		registerRequest.setName("Ankith CR");
		registerRequest.setEmail("ankith@example.com");
		registerRequest.setPassword("password123");

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerRequest)))
				.andExpect(status().isCreated());

		// Now login
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("ankith@example.com");
		loginRequest.setPassword("password123");

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token", notNullValue()))
				.andExpect(jsonPath("$.message", is("Login Successful")));
	}

	@Test
	@DisplayName("POST /api/auth/login — wrong password returns 401")
	void login_WrongPassword_Returns401() throws Exception {
		// Register first
		RegisterRequest registerRequest = new RegisterRequest();
		registerRequest.setName("Ankith CR");
		registerRequest.setEmail("ankith@example.com");
		registerRequest.setPassword("password123");

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerRequest)))
				.andExpect(status().isCreated());

		// Login with wrong password
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("ankith@example.com");
		loginRequest.setPassword("wrongpassword");

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.status", is(401)))
				.andExpect(jsonPath("$.message",
						is("Invalid email or password")));
	}
}