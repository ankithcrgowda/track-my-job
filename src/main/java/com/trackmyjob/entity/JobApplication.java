package com.trackmyjob.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a single job application tracked by a user.
 *
 * This is the "many" side of the User → JobApplication relationship.
 * The foreign key column (user_id) lives in THIS table.
 */

@Entity
@Table(name = "job_applications")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper=false)
public class JobApplication extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * The owning side of the relationship.
	 *
	 * @ManyToOne — many applications belong to one user.
	 * fetch = LAZY — don't auto-load the full User object every time
	 *                we fetch a JobApplication. Load it only if needed.
	 *
	 * @JoinColumn(name = "user_id") — creates the actual FK column
	 * named "user_id" in the job_applications table.
	 * nullable = false — every application must belong to a user.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, length = 150)
	private String companyName;

	@Column(nullable = false, length = 150)
	private String jobRole;

	/**
	 * @Enumerated(EnumType.STRING) — stores the enum NAME in the DB.
	 * e.g. "APPLIED", "REJECTED" — not 0, 1, 2.
	 * Reason explained in ApplicationStatus.java above.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 150)
	private ApplicationStatus status;

	/**
	 * LocalDate (not LocalDateTime) — we only need the date, not the time.
	 * Maps to a DATE column in PostgreSQL.
	 */
	@Column(nullable = false)
	private LocalDate appliedDate;

	/**
	 * columnDefinition = "TEXT" — PostgreSQL TEXT type.
	 * Unlike VARCHAR, TEXT has no length limit.
	 * Good for notes/comments where length is unpredictable.
	 * nullable = true (default) — notes are optional.
	 */
	@Column(columnDefinition = "TEXT")
	private String description;
}






