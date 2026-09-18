package com.trackmyjob.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * Utility class for all JWT operations.
 *
 * @Component — makes this a Spring-managed bean so it can be
 * injected wherever needed (AuthService, JwtAuthFilter).
 *
 * Think of this class as the hotel key card machine:
 * - generateToken() = printing a new key card
 * - validateToken() = checking if the card is genuine and not expired
 * - extractEmail()  = reading the room number from the card
 */
@Component
public class JwtUtil {

	/**
	 * @Value — reads the value from application.properties.
	 * Spring injects "jwt.secret" property directly into this field.
	 * This is how we avoid hardcoding secrets in Java code.
	 */
	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.expiration}")
	private Long expirationMs;

	/**
	 * Converts the Base64 secret string into a cryptographic key.
	 *
	 * HMAC-SHA256 (HS256) requires a key of at least 256 bits (32 bytes).
	 * Our secret in application.properties is Base64-encoded to meet this requirement.
	 * Keys.hmacShaKeyFor() creates the actual SecretKey object JJWT needs.
	 *
	 * Why private? This is an internal helper — no one outside needs it.
	 */
	private SecretKey getSignKey() {
		byte[] keyBytes = Base64.getDecoder().decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * Generates a JWT token for the given email.
	 *
	 * Token structure:
	 *   subject  = user's email (used to identify the user on each request)
	 *   issuedAt = current timestamp
	 *   expiration = issuedAt + 24 hours
	 *   signature = HMAC-SHA256 using our secret key
	 *
	 * Why email as subject and not userId?
	 * Email is human-readable and useful for debugging.
	 * We'll look up the userId from DB using the email anyway.
	 */
	public String generateToken(String email) {
		return Jwts.builder()
				.subject(email)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expirationMs))
				.signWith(getSignKey())
				.compact();
	}

	/**
	 * Extracts all claims (payload data) from a token.
	 *
	 * Claims are the data stored inside the token.
	 * If the token is tampered with or the signature doesn't match,
	 * JJWT throws an exception here — token is rejected.
	 *
	 * Private helper used by the public methods below.
	 */
	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSignKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	/**
	 * Extracts the email (subject) from the token.
	 * Used by JwtAuthFilter to identify which user is making the request.
	 */
	public String extractEmail(String token) {
		return extractAllClaims(token).getSubject();
	}

	/**
	 * Extracts the expiration date from the token.
	 */
	public Date extractExpiration(String token) {
		return extractAllClaims(token).getExpiration();
	}

	/**
	 * Checks if the token has expired.
	 * .before(new Date()) = "is expiration date before right now?"
	 */
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	/**
	 * Full token validation:
	 * 1. Does the email in the token match the expected email?
	 * 2. Is the token not expired?
	 *
	 * Called by JwtAuthFilter on every protected request.
	 */
	public boolean validateToken(String token, String email) {
		final String extrctedEmail = extractEmail(token);
		return extrctedEmail.equals(email) && !isTokenExpired(token);
	}
}









