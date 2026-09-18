package com.trackmyjob.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * A base class shared by all entities.
 *
 * @MappedSuperclass — tells JPA:
 * "This class is NOT its own table. Its fields are inherited
 *  by child entity classes and added to THEIR tables."
 *
 * Think of it as a template. Every entity that extends this
 * automatically gets created_at and updated_at columns.
 * No code duplication. One place to change if needed later.
 *
 * Why not @Entity on this class?
 * Because we don't want a "base_entity" table in the DB.
 * We just want its fields merged into child tables.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

	/**
	 * @PrePersist — JPA lifecycle callback.
	 * This method runs automatically BEFORE an INSERT statement.
	 * We set both timestamps here on first save.
	 *
	 * @PreUpdate — runs automatically BEFORE an UPDATE statement.
	 * We only update updatedAt here — createdAt stays frozen.
	 *
	 * Why use lifecycle callbacks instead of @CreationTimestamp?
	 * More portable (pure JPA, no Hibernate-specific annotations),
	 * and avoids the Hibernate 6.6 @ValueGenerationType conflict
	 * you just encountered.
	 */
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
