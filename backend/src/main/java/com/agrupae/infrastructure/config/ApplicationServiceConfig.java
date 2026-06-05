package com.agrupae.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.authentication.*;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.application.service.assignment.ArchiveAssignmentService;
import com.agrupae.application.service.assignment.CreateAssignmentService;
import com.agrupae.application.service.authentication.*;
import com.agrupae.application.service.course.ArchiveCourseService;
import com.agrupae.application.service.course.CreateCourseService;
import com.agrupae.application.service.course.GetACourseService;
import com.agrupae.application.service.course.JoinCourseService;
import com.agrupae.application.service.course.TransferLeadershipService;
import com.agrupae.application.service.user.GetUserProfileService;
import com.agrupae.application.service.user.UpdateProfileService;
import com.agrupae.application.service.course.GetCoursesService;
import com.agrupae.application.service.course.GetMembersService;

@Configuration
public class ApplicationServiceConfig {

    @Bean
    public LoginService loginService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            TokenProvider tokenProvider,
            TokenHasher tokenHasher,
            TokenConfig tokenConfig) {
        return new LoginService(userRepository, refreshTokenRepository,
                passwordEncoder, tokenProvider, tokenHasher, tokenConfig);
    }

    @Bean
    public SignupService signupService(
            TokenProvider tokenProvider,
            TokenHasher tokenHasher,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RefreshTokenRepository refreshTokenRepository,
            TokenConfig tokenConfig) {
        return new SignupService(tokenProvider, tokenHasher, userRepository,
                passwordEncoder, refreshTokenRepository, tokenConfig);
    }

    @Bean
    public RefreshService refreshService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            TokenProvider tokenProvider,
            TokenHasher tokenHasher,
            TokenConfig tokenConfig) {
        return new RefreshService(userRepository, refreshTokenRepository,
                tokenProvider, tokenHasher, tokenConfig);
    }

    @Bean
    public LogoutService logoutService(
            RefreshTokenRepository refreshTokenRepository,
            TokenHasher tokenHasher) {
        return new LogoutService(refreshTokenRepository, tokenHasher);
    }

    @Bean
    public GetUserProfileService getUserProfileService(UserRepository userRepository) {
        return new GetUserProfileService(userRepository);
    }

    @Bean
    public UpdateProfileService updateProfileService(UserRepository userRepository) {
        return new UpdateProfileService(userRepository);
    }

    @Bean
    public CreateCourseService createCourseService(CourseRepository courseRepository, CourseMembershipRepository courseMembershipRepository) {
        return new CreateCourseService(courseRepository, courseMembershipRepository);
    }

    @Bean
    public JoinCourseService joinCourseService(
            CourseRepository courseRepository,
            CourseMembershipRepository courseMembershipRepository) {
        return new JoinCourseService(courseRepository, courseMembershipRepository);
    }

    @Bean
    public ArchiveCourseService archiveCourseService(CourseRepository courseRepository, AssignmentRepository assignmentRepository) {
        return new ArchiveCourseService(courseRepository, assignmentRepository);
    }

    @Bean
    public ArchiveAssignmentService archiveAssignmentService(AssignmentRepository assignmentRepository, CourseRepository courseRepository) {
        return new ArchiveAssignmentService(assignmentRepository, courseRepository);
    }

    @Bean 
    public GetCoursesService getCoursesService(
            CourseMembershipRepository courseMembershipRepository,
            CourseRepository courseRepository) {
        return new GetCoursesService(courseMembershipRepository, courseRepository);
    }

    @Bean 
    public TransferLeadershipService transferLeadershipService(CourseRepository courseRepository,
    CourseMembershipRepository courseMembershipRepository) {
        return new TransferLeadershipService(courseRepository, courseMembershipRepository);
    }

    @Bean 
    public GetACourseService getACourseService(CourseRepository courseRepository, CourseMembershipRepository courseMembershipRepository) {
        return new GetACourseService(courseRepository, courseMembershipRepository);
    }

    @Bean 
    public GetMembersService getMembersService(CourseMembershipRepository courseMembershipRepository, UserRepository userRepository, CourseRepository courseRepository) {
        return new GetMembersService(courseMembershipRepository, userRepository, courseRepository);
    }
    
    @Bean
    public CreateAssignmentService createAssignmentService(AssignmentRepository assignmentRepository, CourseRepository courseRepository) {
        return new CreateAssignmentService(assignmentRepository, courseRepository);
    }
}