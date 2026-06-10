package com.agrupae.application.service.group;

import java.util.UUID;

import com.agrupae.application.exception.assignment.AssignmentNotFoundException;
import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.group.AssignmentArchivedException;
import com.agrupae.application.exception.group.GroupDissolutionNotAllowedException;
import com.agrupae.application.exception.group.GroupNotFoundException;
import com.agrupae.application.port.in.group.DissolveGroupUseCase;
import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.domain.assignment.Assignment;
import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.group.Group;
import com.agrupae.domain.role.Role;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DissolveGroupService implements DissolveGroupUseCase {
    private final CourseRepository courseRepository;
    private final CourseMembershipRepository courseMembershipRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;

    @Override
    @Transactional
    public void handle(UUID actorId, Role actorRole, UUID courseId, UUID assignmentId, UUID groupId) {
        Course course = this.courseRepository.findById(courseId);
        if (course == null) {
            throw new CourseNotFoundException();
        }

        boolean isCourseLeader = course.getLeaderId().equals(actorId);
        boolean isAdmin = actorRole == Role.ADMIN;
        if (!isCourseLeader && !isAdmin && !this.courseMembershipRepository.exists(actorId, courseId)) {
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

        AssignmentFlags flags = assignment.getAssignmentFlags();
        boolean dissolvingAsGroupLeader = group.getLeaderId().equals(actorId) && flags.groupLeaderCanDissolve();
        boolean dissolvingAsSupervisor = (isCourseLeader || isAdmin) && flags.supervisorCanEditGroups();
        
        if (!dissolvingAsGroupLeader && !dissolvingAsSupervisor) {
            throw new GroupDissolutionNotAllowedException();
        }

        this.groupRepository.deleteById(groupId);
    }
}
