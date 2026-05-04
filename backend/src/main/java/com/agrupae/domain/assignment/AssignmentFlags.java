package com.agrupae.domain.assignment;

import lombok.Getter;

@Getter
public class AssignmentFlags {
    private int maxGroupMembers;
    private int maxGroups;
    private boolean studentsCanCreateGroups;
    private boolean studentsCanLeaveGroups;
    private boolean groupLeaderCanDissolve;
    private boolean groupLeaderCanRemoveMembers;
    private boolean groupLeaderCanChangeMode;
    private boolean groupLeaderCanTransferLeadership;
    private boolean supervisorCanEditGroups;

    public AssignmentFlags(
        int maxGroupMembers,
        int maxGroups,
        boolean studentsCanCreateGroups,
        boolean studentsCanLeaveGroups,
        boolean groupLeaderCanDissolve,
        boolean groupLeaderCanRemoveMembers,
        boolean groupLeaderCanChangeMode,
        boolean groupLeaderCanTransferLeadership,
        boolean supervisorCanEditGroups
    ) throws ForbiddenFlagCombination {
        if (!studentsCanCreateGroups && !supervisorCanEditGroups) {
            throw new ForbiddenFlagCombination("FORBIDDEN: either studentsCanCreateGroups or supervisorCanEditGroups must be true.");
        }

        if (!studentsCanLeaveGroups && groupLeaderCanDissolve) {
            throw new ForbiddenFlagCombination("FORBIDDEN: studentsCanLeaveGroups must be true when groupLeaderCanDissolve is true.");
        }

        this.maxGroupMembers = maxGroupMembers;
        this.maxGroups = maxGroups;
        this.studentsCanCreateGroups = studentsCanCreateGroups;
        this.studentsCanLeaveGroups = studentsCanLeaveGroups;
        this.groupLeaderCanDissolve = groupLeaderCanDissolve;
        this.groupLeaderCanRemoveMembers = groupLeaderCanRemoveMembers;
        this.groupLeaderCanChangeMode = groupLeaderCanChangeMode;
        this.groupLeaderCanTransferLeadership = groupLeaderCanTransferLeadership;
        this.supervisorCanEditGroups = supervisorCanEditGroups;
    }
}
