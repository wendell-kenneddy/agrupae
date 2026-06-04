package com.agrupae.infrastructure.persistence.jpa.model.assignment;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "assignments")
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PROTECTED)
public class AssignmentJpaEntity {
    @Id
    private UUID id;
    @Column(name = "course_id", nullable = false)
    private UUID courseId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Embedded
    private AssignmentFlagsEmbeddable assignmentFlags;
    @Column(nullable = false)
    private boolean archived;
    @Column(nullable = false)
    private Instant dueDate;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

}
