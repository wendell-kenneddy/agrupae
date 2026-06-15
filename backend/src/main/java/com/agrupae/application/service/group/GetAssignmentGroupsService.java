package com.agrupae.application.service.group;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.port.in.group.AssignmentGroupsView;
import com.agrupae.application.port.in.group.GetAssignmentGroupsUseCase;
import com.agrupae.application.port.in.group.GroupSummaryView;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.group.Group;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAssignmentGroupsService implements GetAssignmentGroupsUseCase {
    private final CourseRepository courseRepository;
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    public AssignmentGroupsView handle(
            @NonNull UUID userId,
            @NonNull UUID courseId,
            @NonNull UUID assignmentId,
            @NonNull Pageable pageable) {

        Course course = this.courseRepository.findById(courseId);
        if (course == null || !this.courseMembershipRepository.exists(userId, courseId)) {
            throw new CourseNotFoundException();
        }

        Assignment assignment = this.assignmentRepository.findById(assignmentId);
        if (assignment == null || !assignment.getCourseId().equals(courseId)) {
            throw new AssignmentNotFoundException();
        }

        UUID myGroupId = this.groupMemberRepository.findGroupIdByAssignmentIdAndMemberId(assignmentId, userId);
        GroupSummaryView myGroupView = null;
        if (myGroupId != null) {
            Group myGroup = this.groupRepository.findById(myGroupId);
            if (myGroup != null) {
                int count = this.groupMemberRepository.countByGroupId(myGroupId);
                myGroupView = toSummaryView(myGroup, count);
            }
        }

        Page<Group> groupsPage = this.groupRepository.findByAssignmentId(assignmentId, pageable);
        Page<GroupSummaryView> groupsViewPage = groupsPage.map(group -> {
            int count = this.groupMemberRepository.countByGroupId(group.getId());
            return toSummaryView(group, count);
        });

        return new AssignmentGroupsView(myGroupView, groupsViewPage);
    }

    private GroupSummaryView toSummaryView(Group group, int memberCount) {
        return new GroupSummaryView(
                group.getId(),
                group.getAssignmentId(),
                group.getLeaderId(),
                group.getName(),
                group.isOpen(),
                group.isMembersCanEditArtifacts(),
                memberCount,
                group.getCreatedAt(),
                group.getUpdatedAt());
    }
}
