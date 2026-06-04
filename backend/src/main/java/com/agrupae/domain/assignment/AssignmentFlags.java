package com.agrupae.domain.assignment;

public record AssignmentFlags(
        int maxGroupMembers,
        int maxGroups,
        boolean studentsCanCreateGroups,
        boolean studentsCanLeaveGroups,
        boolean groupLeaderCanDissolve,
        boolean groupLeaderCanRemoveMembers,
        boolean groupLeaderCanChangeMode,
        boolean groupLeaderCanTransferLeadership,
        boolean supervisorCanEditGroups) {
    public AssignmentFlags {
        if (maxGroupMembers <= 0) {
            throw new ForbiddenFlagCombination("FORBIDDEN: maxGroupMembers must be greater than zero.");
        }

        if (maxGroups <= 0) {
            throw new ForbiddenFlagCombination("FORBIDDEN: maxGroups must be greater than zero.");
        }

        if (!studentsCanCreateGroups && !supervisorCanEditGroups) {
            throw new ForbiddenFlagCombination(
                    "FORBIDDEN: either studentsCanCreateGroups or supervisorCanEditGroups must be true.");
        }

        if (!studentsCanLeaveGroups && groupLeaderCanDissolve) {
            throw new ForbiddenFlagCombination(
                    "FORBIDDEN: studentsCanLeaveGroups must be true when groupLeaderCanDissolve is true.");
        }
    }
}
