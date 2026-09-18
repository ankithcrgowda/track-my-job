package com.trackmyjob.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter.
 *
 * Extends OncePerRequestFilter — guarantees this filter runs
 * EXACTLY ONCE per request (not multiple times for forwarded requests).
 *
 * Execution flow for every incoming request:
 *
 * 1. Read the Authorization header
 * 2. If no header or doesn't start with "Bearer " → skip (let Spring Security handle it)
 * 3. Extract the token (remove "Bearer " prefix)
 * 4. Extract email from token
 * 5. Load user from DB using email
 * 6. Validate token against user
 * 7. If valid → set Authentication in SecurityContext
 * 8. Continue the filter chain
 *
 * The SecurityContext is like a "current user" holder for the
 * duration of this request. Once we set authentication here,
 * Spring Security knows the request is authenticated.
 * The Controller can then read the current user from it.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final CustomUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain filterChain)
			throws ServletException, IOException {

		// Step 1 - Read Authorization header
		final String authHeader = request.getHeader("Authorization");

		// Step 2 - If missing or wrong format, skip this filter entirely
		// The request continues but without authentication set.
		// Spring Security will then reject it if the endpoint requires auth.
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		// Step 3 — Extract JWT token (strip "Bearer " prefix — 7 characters)
		final String token = authHeader.substring(7);
		String email = null;

		try {
			// Step 4 — Extract email from token
			email = jwtUtil.extractEmail(token);
		} catch (Exception e) {
			// Token is malformed or signature is invalid
			// We just let it fall through — Spring Security
			// will return 401 since no authentication is set
			filterChain.doFilter(request, response);
			return;
		}

		// Step 5 — Only proceed if email extracted AND no auth set yet
		// SecurityContextHolder.getContext().getAuthentication() == null
		// means "no one is authenticated for this request yet"
		if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			// Step 5 — Load user from DB
			UserDetails userDetails = userDetailsService.loadUserByUsername(email);

			// Step 6 — Validate token
			if(jwtUtil.validateToken(token, userDetails.getUsername())) {
				// Step 7 — Create authentication token
				// CcijrCfZBuqDzBWp3qSrBEZCqBUfQVz4CWGHWF91iaEw is Spring Security's
				// standard object representing an authenticated user.
				// Third parameter = authorities/roles
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userDetails,
						null, // credentials = null (we don't need password after auth)
						userDetails.getAuthorities()
				);
				// Attach request details (IP address, session info) to the auth token
				authToken.setDetails(
						new WebAuthenticationDetailsSource()
								.buildDetails(request)
				);
				// Step 8 - Set in SecurityContext
				// From this point on, Spring Security treats this request
				// as authenticated for the rest of its lifecycle
				SecurityContextHolder.getContext().setAuthentication(authToken);

			}
		}
		// Step 9 - Always continue the filter chain
		filterChain.doFilter(request, response);
	}
}









