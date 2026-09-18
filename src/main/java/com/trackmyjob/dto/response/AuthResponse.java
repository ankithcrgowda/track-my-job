package com.trackmyjob.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Returned after successful register or login.
 *
 * Phase 3: message field only ("Login successful")
 * Phase 4: we add a real JWT token field here
 *
 * Using @Builder so we can construct it cleanly:
 *   AuthResponse.builder().message("Login successful").build()
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

	private String message;
	private String email;
	private String name;
	private String token;

}
