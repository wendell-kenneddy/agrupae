package com.agrupae.application.service.group;

import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupModeChangeNotAllowedException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.NotGroupLeaderException;
import com.agrupae.application.port.in.group.ChangeGroupModeUseCase;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupEntryRequestRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.group.Group;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChangeGroupModeService implements ChangeGroupModeUseCase {
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;
    private final GroupEntryRequestRepository groupEntryRequestRepository;

    @Override
    @Transactional
    public void handle(UUID courseId, UUID assignmentId, UUID groupId, UUID userId, boolean open) {
        if (!this.courseMembershipRepository.exists(userId, courseId)) {
            throw new CourseNotFoundException();
        }

        Assignment assignment = this.assignmentRepository.findById(assignmentId);
        if (assignment == null || !assignment.getCourseId().equals(courseId)) {
            throw new AssignmentNotFoundException();
        }

        if (assignment.isArchived()) {
            throw new AssignmentArchivedException();
        }

        Group group = this.groupRepository.findById(groupId);
        if (group == null || !group.getAssignmentId().equals(assignmentId)) {
            throw new GroupNotFoundException();
        }

        if (!group.getLeaderId().equals(userId)) {
            throw new NotGroupLeaderException();
        }

        if (!assignment.getAssignmentFlags().groupLeaderCanChangeMode()) {
            throw new GroupModeChangeNotAllowedException();
        }

        if (group.isOpen() == open) {
            return;
        }

        group.changeMode(open);
        this.groupRepository.save(group);
        this.groupEntryRequestRepository.deleteAllPendingByGroupId(groupId);
    }
}
