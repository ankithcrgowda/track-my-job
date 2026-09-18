package com.trackmyjob.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a registered user in the system.
 *
 * @Entity — tells Hibernate "this class maps to a database table"
 *           Same concept as annotating a POJO in plain Hibernate.
 *
 * @Table  — specifies the exact table name in PostgreSQL.
 *           Without this, Hibernate uses the class name ("User").
 *           We explicitly name it "users" — lowercase, plural.
 *           Why? "user" is a reserved keyword in PostgreSQL.
 *           Without quotes it causes SQL errors. Naming it "users" avoids this.
 */
@Entity
@Table(name="users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
// callSuper = false — tells Lombok's equals/hashCode NOT to include
// BaseEntity fields. We only compare users by their own fields.
public class User extends BaseEntity{

	/**
	 * Primary Key.
	 *
	 * @Id — marks this as the primary key (you know this from Hibernate)
	 *
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * Tells Hibernate to let the DATABASE generate the ID.
	 * PostgreSQL uses BIGSERIAL (auto-increment) for this.
	 * Every time you insert a row, PostgreSQL assigns the next ID automatically.
	 *
	 * Why IDENTITY and not SEQUENCE or AUTO?
	 * IDENTITY delegates to the DB's native auto-increment — most efficient for PostgreSQL.
	 * SEQUENCE creates a separate sequence object (more control, but unnecessary here).
	 * AUTO lets Hibernate decide — unpredictable, avoid it.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * @Column — maps this field to a specific column.
	 * nullable = false → adds a NOT NULL constraint in the DB schema.
	 * length = 100     → sets VARCHAR(100) in the DB.
	 *
	 * Without @Column, Hibernate uses the field name and defaults.
	 * Being explicit is a good habit — you control exactly what the DB looks like.
	 */
	@Column(nullable = false, length = 100)
	private String name;

	/**
	 * unique = true → adds a UNIQUE constraint in the DB.
	 * No two users can have the same email — enforced at DB level.
	 * We also validate this at the service level (Phase 6).
	 * Defense in depth — validate at multiple layers.
	 */
	@Column(nullable = false, unique = true)
	private String email;

	/**
	 * The hashed password. NEVER store plain text passwords.
	 * We will use BCryptPasswordEncoder in Phase 4 (Spring Security).
	 * BCrypt output is always 60 characters — length = 60 is exact.
	 */
	@Column(nullable = false, length = 60)
	private String password;

	/**
	 * The one-to-many relationship.
	 *
	 * One User → Many JobApplications.
	 *
	 * mappedBy = "user" — tells Hibernate:
	 * "The foreign key is managed by the 'user' field in JobApplication.
	 *  Don't create a separate join table — the FK column lives in job_applications."
	 *
	 * cascade = CascadeType.ALL — if we delete a User, delete all their applications too.
	 * orphanRemoval = true      — if we remove an application from this list, delete it from DB.
	 *
	 * fetch = FetchType.LAZY (default for collections) — don't load all applications
	 * every time we load a User. Load them only when explicitly accessed.
	 * This is critical for performance — you know this concept from Hibernate.
	 */

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	// @Builder.Default needed because @Builder would set this to null otherwise.
	// We want an empty list, not null, to avoid NullPointerExceptions.
	private List<JobApplication> jobApplication = new ArrayList<>();
}










