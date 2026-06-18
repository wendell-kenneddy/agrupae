package com.agrupae.application.service.group;

import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseArchivedException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.exception.group.NotGroupLeaderException;
import com.agrupae.application.port.in.group.EditGroupUseCase;
import com.agrupae.application.port.in.group.GroupView;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.group.Group;

import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EditGroupService implements EditGroupUseCase {
    private final CourseRepository courseRepository;
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;

    @Override
    @Transactional
    public GroupView handle(
            @NonNull UUID courseId,
            @NonNull UUID assignmentId,
            @NonNull UUID groupId,
            @NonNull UUID userId,
            @NonNull String name) {
        Course course = this.courseRepository.findById(courseId);

        if (course == null) {
            throw new CourseNotFoundException();
        }

        if (!this.courseMembershipRepository.exists(userId, courseId)) {
            throw new CourseNotFoundException();
        }

        if (course.isArchived()) {
            throw new CourseArchivedException();
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

        group.editName(name);
        this.groupRepository.save(group);

        return new GroupView(
                group.getId(),
                group.getAssignmentId(),
                group.getLeaderId(),
                group.getName(),
                group.isOpen(),
                group.isMembersCanEditArtifacts(),
                group.getCreatedAt(),
                group.getUpdatedAt());
    }
}
