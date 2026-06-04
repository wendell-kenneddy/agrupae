package com.agrupae.domain.assignment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssignmentFlagsTest {

    @Test
    void withValidCombination_constructsSuccessfully() {
        assertThatCode(() -> new AssignmentFlags(4, 10, true, true, false, false, false, false, false))
                .doesNotThrowAnyException();

        AssignmentFlags flags = new AssignmentFlags(4, 10, true, true, false, false, false, false, false);
        assertThat(flags.maxGroupMembers()).isEqualTo(4);
        assertThat(flags.maxGroups()).isEqualTo(10);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void withMaxGroupMembersNotPositive_throws(int maxGroupMembers) {
        assertThatThrownBy(() -> new AssignmentFlags(maxGroupMembers, 10, true, true, false, false, false, false, false))
                .isInstanceOf(ForbiddenFlagCombination.class)
                .hasMessage("FORBIDDEN: maxGroupMembers must be greater than zero.");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void withMaxGroupsNotPositive_throws(int maxGroups) {
        assertThatThrownBy(() -> new AssignmentFlags(4, maxGroups, true, true, false, false, false, false, false))
                .isInstanceOf(ForbiddenFlagCombination.class)
                .hasMessage("FORBIDDEN: maxGroups must be greater than zero.");
    }

    @Test
    void withNeitherStudentsCreateNorSupervisorEdit_throws() {
        assertThatThrownBy(() -> new AssignmentFlags(4, 10, false, true, false, false, false, false, false))
                .isInstanceOf(ForbiddenFlagCombination.class)
                .hasMessage("FORBIDDEN: either studentsCanCreateGroups or supervisorCanEditGroups must be true.");
    }

    @Test
    void withGroupLeaderCanDissolveButStudentsCannotLeave_throws() {
        assertThatThrownBy(() -> new AssignmentFlags(4, 10, true, false, true, false, false, false, false))
                .isInstanceOf(ForbiddenFlagCombination.class)
                .hasMessage("FORBIDDEN: studentsCanLeaveGroups must be true when groupLeaderCanDissolve is true.");
    }
}
