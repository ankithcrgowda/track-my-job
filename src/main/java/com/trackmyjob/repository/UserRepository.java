package com.trackmyjob.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trackmyjob.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByEmail(String email);
}
