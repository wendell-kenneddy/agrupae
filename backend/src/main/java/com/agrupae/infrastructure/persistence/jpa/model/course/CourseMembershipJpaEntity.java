package com.agrupae.infrastructure.persistence.jpa.model.course;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "users_courses")
@Entity
@IdClass(CourseMembershipId.class)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter(value = AccessLevel.PROTECTED)
public class CourseMembershipJpaEntity {
    @Id
    @Column(nullable = false)
    private UUID studentId;
    @Id
    @Column(nullable = false)
    private UUID courseId;
    @Column(nullable = false)
    private Instant createdAt;
}
