package com.agrupae.application.service.course;

import java.util.UUID;

import com.agrupae.application.exception.course.CourseNotFoundException;
import com.agrupae.application.exception.course.TargetUserNotEnrolled;
import com.agrupae.application.exception.course.NotAuthorizedToTransferLeadershipException;
import com.agrupae.application.port.in.course.TransferLeadershipUseCase;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.domain.course.Course;
import com.agrupae.domain.role.Role;
import com.agrupae.application.port.in.course.CourseView;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class TransferLeadershipService implements TransferLeadershipUseCase {
    private final CourseRepository courseRepository;
    private final CourseMembershipRepository courseMembershipRepository;
    
    @Override
    public CourseView handle(@NonNull UUID actorId, @NonNull Role actorRole, @NonNull UUID courseId, @NonNull UUID newLeaderId) {
        Course course = courseRepository.findById(courseId);

        if (course == null)
            throw new CourseNotFoundException();

        if (actorRole != Role.ADMIN && !course.getLeaderId().equals(actorId))
            throw new NotAuthorizedToTransferLeadershipException();

        if (!courseMembershipRepository.exists(newLeaderId, courseId)) 
            throw new TargetUserNotEnrolled();

        course.transferLeadership(newLeaderId);
        this.courseRepository.save(course);

        return new CourseView(course.getId(), course.getLeaderId(), 
            course.getName(), course.getDescription(),
            course.getInviteCode(), course.isArchived(),
            course.getCreatedAt(), course.getUpdatedAt()
        );
    }
}
