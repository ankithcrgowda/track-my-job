package com.trackmyjob.config;

import com.trackmyjob.security.CustomUserDetailsService;
import com.trackmyjob.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Full JWT Security Configuration
 *
 * @EnableWebSecurity — activates Spring Security's web security support
 * and provides the Spring MVC integration.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


	private final JwtAuthFilter jwtAuthFilter;
	private final CustomUserDetailsService userDetailsService;
	/**
	 * BCryptPasswordEncoder is the industry standard for hashing passwords.
	 *
	 * How BCrypt works (interview-worthy explanation):
	 * - Takes your plain password + a random "salt"
	 * - Runs it through a slow hashing algorithm (intentionally slow —
	 *   makes brute force attacks impractical)
	 * - Produces a 60-character hash like: $2a$10$xxxxxxxxxxx...
	 * - The hash INCLUDES the salt, so matches() can verify without
	 *   storing the salt separately
	 *
	 * Why @Bean? So Spring can inject PasswordEncoder wherever needed
	 * (like in AuthServiceImpl). Without @Bean, Spring doesn't know
	 * this object exists.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * AuthenticationProvider — the component that actually verifies
	 * username + password during login.
	 *
	 * DaoAuthenticationProvider is Spring's standard implementation:
	 * - Uses our CustomUserDetailsService to load the user
	 * - Uses BCryptPasswordEncoder to verify the password
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	/**
	 * AuthenticationManager — the entry point for authentication.
	 * Used in AuthServiceImpl to authenticate login requests.
	 * Spring Boot auto-configures this — we just expose it as a bean.
	 */
	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	/**
	 * The Security Filter Chain — the heart of our security config.
	 *
	 * This defines:
	 * 1. Which endpoints are public (no token needed)
	 * 2. Which endpoints are protected (token required)
	 * 3. That we use STATELESS sessions (no server-side session — JWT handles state)
	 * 4. Where our JwtAuthFilter sits in the filter chain
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http)
			throws Exception {
		http
				// Disable CSRF — not needed for REST APIs with JWT
				// CSRF protects browser-based form submissions.
				// Our API clients (Postman, mobile apps) send JWT in headers — no CSRF risk.
				.csrf(AbstractHttpConfigurer::disable)

				// Define authorization rules
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/auth/**").permitAll()   // register & login must be public
						.requestMatchers(
								"/swagger-ui/**",
								"/v3/api-docs/**",
								"/webjars/**"
						).permitAll() // optional, for Swagger
						.anyRequest().authenticated()
				)

				// STATELESS — no HttpSession created or used.
				// Each request must carry its own JWT token.
				// The server remembers nothing between requests.
				// This is what makes JWT-based APIs scalable and cloud-friendly.
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)

				// Register our AuthenticationProvider
				.authenticationProvider(authenticationProvider())

				// Add JwtAuthFilter BEFORE Spring's default
				// UsernamePasswordAuthenticationFilter.
				// Why before? Our filter must authenticate the user via JWT
				// BEFORE Spring's filter tries to redirect to a login page.
				.addFilterBefore(jwtAuthFilter,
						UsernamePasswordAuthenticationFilter.class);

		return http.build();


//	/**
//	 * TEMPORARY — Phase 3 only.
//	 * Disables all security so we can test with Postman.
//	 * This will be completely replaced in Phase 4.
//	 */
//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		http
//				.csrf(AbstractHttpConfigurer::disable)
//				.authorizeHttpRequests(auth ->auth
//						.anyRequest().permitAll()
//				);
//		return http.build();
	}
}






