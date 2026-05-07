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
