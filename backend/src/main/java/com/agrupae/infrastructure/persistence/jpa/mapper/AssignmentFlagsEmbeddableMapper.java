package com.agrupae.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.infrastructure.persistence.jpa.model.assignment.AssignmentFlagsEmbeddable;

@Component
public class AssignmentFlagsEmbeddableMapper {
    public AssignmentFlags toDomain(AssignmentFlagsEmbeddable embeddable) {
        return new AssignmentFlags(
            embeddable.maxGroupMembers(),
            embeddable.maxGroups(),
            embeddable.studentsCanCreateGroups(),
            embeddable.studentsCanLeaveGroups(),
            embeddable.groupLeaderCanDissolve(),
            embeddable.groupLeaderCanRemoveMembers(),
            embeddable.groupLeaderCanChangeMode(),
            embeddable.groupLeaderCanTransferLeadership(),
            embeddable.supervisorCanEditGroups());
    }

    public AssignmentFlagsEmbeddable toEmbeddable(AssignmentFlags assignmentFlags) {
        return new AssignmentFlagsEmbeddable(
           assignmentFlags.maxGroupMembers(),
            assignmentFlags.maxGroups(),
            assignmentFlags.studentsCanCreateGroups(),
            assignmentFlags.studentsCanLeaveGroups(),
            assignmentFlags.groupLeaderCanDissolve(),
            assignmentFlags.groupLeaderCanRemoveMembers(),
            assignmentFlags.groupLeaderCanChangeMode(),
            assignmentFlags.groupLeaderCanTransferLeadership(),
            assignmentFlags.supervisorCanEditGroups());
    }
}
