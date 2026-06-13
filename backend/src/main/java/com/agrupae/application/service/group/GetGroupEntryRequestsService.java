package com.agrupae.application.service.group;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.NotGroupLeaderException;
import com.agrupae.application.port.in.group.GetGroupEntryRequestsUseCase;
import com.agrupae.application.port.in.group.GroupEntryRequestView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.application.port.out.group.GroupEntryRequestRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.group.GroupEntryRequest;
import com.agrupae.domain.group.GroupEntryRequestStatus;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;

@RequiredArgsConstructor
public class GetGroupEntryRequestsService implements GetGroupEntryRequestsUseCase {
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;
    private final GroupEntryRequestRepository groupEntryRequestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GroupEntryRequestView> handle(@NonNull UUID courseId, @NonNull UUID assignmentId, @NonNull UUID groupId, @NonNull UUID userId,
            GroupEntryRequestStatus status) {
        if (!this.courseMembershipRepository.exists(userId, courseId)) {
            throw new CourseNotFoundException();
        }

        Assignment assignment = this.assignmentRepository.findById(assignmentId);
        if (assignment == null || !assignment.getCourseId().equals(courseId)) {
            throw new AssignmentNotFoundException();
        }

        Group group = this.groupRepository.findById(groupId);
        if (group == null || !group.getAssignmentId().equals(assignmentId)) {
            throw new GroupNotFoundException();
        }

        if (!group.getLeaderId().equals(userId)) {
            throw new NotGroupLeaderException();
        }

        List<GroupEntryRequest> requests;

        if (status != null) {
            requests = this.groupEntryRequestRepository.findByGroupIdAndStatus(groupId, status);
        } else {
            requests = this.groupEntryRequestRepository.findByGroupId(groupId);
        }

        return requests.stream()
                .map(request -> new GroupEntryRequestView(
                        request.getId(),
                        request.getGroupId(),
                        request.getUserId(),
                        request.getStatus(),
                        request.getCreatedAt(),
                        request.getUpdatedAt()))
                .collect(Collectors.toList());
    }
}
