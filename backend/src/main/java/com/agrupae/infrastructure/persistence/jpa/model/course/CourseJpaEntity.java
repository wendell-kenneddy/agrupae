package com.agrupae.infrastructure.persistence.jpa.model.course;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "courses")
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter(value = AccessLevel.PROTECTED)
public class CourseJpaEntity {
    @Id
    private UUID id;
    @Column(name = "supervisor_id")
    private UUID leaderId;
    @Column(nullable = false, unique = true)
    private String inviteCode;
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(nullable = false)
    private boolean archived;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;
}
