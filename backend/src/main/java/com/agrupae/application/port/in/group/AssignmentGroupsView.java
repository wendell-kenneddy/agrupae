package com.agrupae.application.port.in.group;

import org.springframework.data.domain.Page;

public record AssignmentGroupsView(
    GroupSummaryView myGroup,
    Page<GroupSummaryView> groups
) {
}
