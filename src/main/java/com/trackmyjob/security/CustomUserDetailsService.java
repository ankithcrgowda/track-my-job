package com.trackmyjob.security;

import com.trackmyjob.entity.User;
import com.trackmyjob.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Bridges our User entity with Spring Security's authentication system.
 *
 * Spring Security doesn't know about our User class.
 * It works with a standard interface called UserDetails.
 * This class loads our User from the DB and wraps it as UserDetails
 * so Spring Security can work with it.
 *
 * The method is called loadUserByUsername() — but "username"
 * in our app means "email". Spring Security uses the term
 * "username" generically for whatever identifier you use.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));

		/**
		 * org.springframework.security.core.userdetails.User (Spring's User)
		 * is different from com.trackmyjob.entity.User (our User).
		 *
		 * Spring's User.withUsername() is a builder that creates
		 * a UserDetails object Spring Security understands.
		 *
		 * We pass:
		 *   username = email
		 *   password = the BCrypt hash stored in DB
		 *   roles    = empty for now (we'll add roles in a future phase)
		 *
		 * Spring Security will use this to verify the password
		 * during authentication.
		 */
		return org.springframework.security.core.userdetails.User
				.withUsername(user.getEmail())
				.password(user.getPassword())
				.roles("USER")
				.build();
	}
}







