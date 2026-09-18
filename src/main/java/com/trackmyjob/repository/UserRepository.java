package com.trackmyjob.repository;


import com.trackmyjob.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity.
 *
 * JpaRepository<User, Long> gives us for FREE:
 *   save(user)          → INSERT or UPDATE
 *   findById(id)        → SELECT WHERE id = ?
 *   findAll()           → SELECT * FROM users
 *   deleteById(id)      → DELETE WHERE id = ?
 *   count()             → SELECT COUNT(*)
 *   existsById(id)      → SELECT EXISTS(...)
 *   ... and more
 *
 * We just ADD the custom methods we need on top.
 *
 * Spring Data reads the method NAME and generates SQL automatically.
 * findByEmail → SELECT * FROM users WHERE email = ?
 * existsByEmail → SELECT EXISTS(...) WHERE email = ?
 *
 * This is called "query derivation" — one of Spring Data's
 * most powerful and commonly asked-about interview features.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	// Used during login — find the user to verify their password
	Optional<User> findByEmail(String email);

	// Used during registration — check if email is already taken
	boolean existsByEmail(String email);
}
