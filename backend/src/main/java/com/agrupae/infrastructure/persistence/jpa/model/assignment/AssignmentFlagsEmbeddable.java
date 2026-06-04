package com.agrupae.infrastructure.persistence.jpa.model.assignment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record AssignmentFlagsEmbeddable(
    @Column(name = "max_group_members") int maxGroupMembers,
    @Column(name = "max_group") int maxGroups,
    @Column(name = "students_can_create_groups") boolean studentsCanCreateGroups,
    @Column(name = "students_can_leave_groups") boolean studentsCanLeaveGroups,
    boolean groupLeaderCanDissolve,
    boolean groupLeaderCanRemoveMembers,
    boolean groupLeaderCanChangeMode,
    boolean groupLeaderCanTransferLeadership,
    @Column(name = "course_supervisor_can_edit_assignment") boolean supervisorCanEditGroups
) {

}
