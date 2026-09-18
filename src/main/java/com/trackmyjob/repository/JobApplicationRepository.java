package com.trackmyjob.repository;

import com.trackmyjob.entity.ApplicationStatus;
import com.trackmyjob.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for JobApplication entity.
 *
 * All query methods here are scoped to a specific user.
 * Why? Because users must only see THEIR OWN applications.
 * We enforce this at the repository level — not just the controller.
 * This is called "data scoping" and is critical for security.
 *
 * Spring Data generates these SQL statements automatically:
 *
 * findByUserId(userId)
 *   → SELECT * FROM job_applications WHERE user_id = ?
 *
 * findByIdAndUserId(id, userId)
 *   → SELECT * FROM job_applications WHERE id = ? AND user_id = ?
 *     (prevents user A from accessing user B's application by guessing IDs)
 *
 * findByUserIdAndStatus(userId, status)
 *   → SELECT * FROM job_applications WHERE user_id = ? AND status = ?
 */
@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

//	Get all the application of the userId
	List<JobApplication> findByUserId(Long userId);

	// Get one application — only if it belongs to this user
	// This prevents IDOR (Insecure Direct Object Reference) attacks
	Optional<JobApplication> findByIdAndUserId(Long id, Long userId);

	// Filter applications by status for a specific user
	Optional<JobApplication> findByUserIdAndStatus(Long userId, ApplicationStatus status);
}
