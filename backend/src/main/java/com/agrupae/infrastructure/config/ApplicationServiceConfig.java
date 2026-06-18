package com.agrupae.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.agrupae.application.port.out.assignment.AssignmentRepository;
import com.agrupae.application.port.out.authentication.*;
import com.agrupae.application.port.out.course.CourseMembershipRepository;
import com.agrupae.application.port.out.course.CourseRepository;
import com.agrupae.application.port.out.group.GroupMemberRepository;
import com.agrupae.application.port.out.group.GroupRepository;
import com.agrupae.application.port.out.user.UserRepository;
import com.agrupae.application.port.out.assignment.AssignmentArtifactRepository;
import com.agrupae.application.service.assignment.AddAssignmentArtifactService;
import com.agrupae.application.service.assignment.ArchiveAssignmentService;
import com.agrupae.application.service.assignment.CreateAssignmentService;
import com.agrupae.application.service.assignment.EditAssignmentService;
import com.agrupae.application.service.assignment.EditAssignmentArtifactService;
import com.agrupae.application.service.assignment.DeleteAssignmentArtifactService;
import com.agrupae.application.service.assignment.GetAnAssignmentService;
import com.agrupae.application.service.authentication.*;
import com.agrupae.application.service.course.ArchiveCourseService;
import com.agrupae.application.service.course.CreateCourseService;
import com.agrupae.application.service.course.GetACourseService;
import com.agrupae.application.service.course.JoinCourseService;
import com.agrupae.application.service.course.TransferLeadershipService;
import com.agrupae.application.service.user.GetUserProfileService;
import com.agrupae.application.service.user.UpdateProfileService;
import com.agrupae.application.service.course.GetCoursesService;
import com.agrupae.application.service.course.GetCourseMembersService;
import com.agrupae.application.service.assignment.GetAssignmentArtifactsService;
import com.agrupae.application.service.assignment.GetAssignmentsService;
import com.agrupae.application.port.out.group.GroupEntryRequestRepository;
import com.agrupae.application.service.group.CreateGroupService;
import com.agrupae.application.service.group.GetAssignmentGroupsService;
import com.agrupae.application.service.group.JoinOpenGroupService;
import com.agrupae.application.service.group.RequestGroupEntryService;
import com.agrupae.application.service.group.CancelGroupEntryRequestService;
import com.agrupae.application.service.group.ChangeGroupArtifactDeliverableStatusService;
import com.agrupae.application.service.group.GetUserGroupEntryRequestsService;
import com.agrupae.application.service.group.AcceptGroupEntryRequestService;
import com.agrupae.application.service.group.RejectGroupEntryRequestService;
import com.agrupae.application.service.group.GetGroupEntryRequestsService;
import com.agrupae.application.service.group.ChangeGroupModeService;
import com.agrupae.application.service.group.DissolveGroupService;
import com.agrupae.application.service.group.LeaveGroupService;
import com.agrupae.application.service.group.RemoveGroupMemberService;
import com.agrupae.application.port.out.group.GroupArtifactRepository;
import com.agrupae.application.service.group.AddGroupArtifactService;
import com.agrupae.application.service.group.GetGroupArtifactsService;
import com.agrupae.application.service.group.GetGroupMembersService;
import com.agrupae.application.service.group.GetPublicGroupArtifactsService;
import com.agrupae.application.service.group.EditGroupArtifactService;
import com.agrupae.application.service.group.DeleteGroupArtifactService;
import com.agrupae.application.service.group.ChangeGroupArtifactPrivacyService;
import com.agrupae.application.service.group.EditGroupService;

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
        public CreateCourseService createCourseService(CourseRepository courseRepository,
                        CourseMembershipRepository courseMembershipRepository) {
                return new CreateCourseService(courseRepository, courseMembershipRepository);
        }

        @Bean
        public JoinCourseService joinCourseService(
                        CourseRepository courseRepository,
                        CourseMembershipRepository courseMembershipRepository) {
                return new JoinCourseService(courseRepository, courseMembershipRepository);
        }

        @Bean
        public GetAssignmentsService getAssignmentsService(
                        CourseRepository courseRepository,
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository) {
                return new GetAssignmentsService(courseRepository, courseMembershipRepository, assignmentRepository);
        }

        @Bean
        public GetAnAssignmentService getAnAssignmentService(
                        CourseRepository courseRepository,
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository) {
                return new GetAnAssignmentService(courseRepository, courseMembershipRepository, assignmentRepository);
        }

        @Bean
        public ArchiveCourseService archiveCourseService(CourseRepository courseRepository,
                        AssignmentRepository assignmentRepository,
                        CourseMembershipRepository courseMembershipRepository) {
                return new ArchiveCourseService(courseRepository, assignmentRepository, courseMembershipRepository);
        }

        @Bean
        public ArchiveAssignmentService archiveAssignmentService(AssignmentRepository assignmentRepository,
                        CourseRepository courseRepository, CourseMembershipRepository courseMembershipRepository) {
                return new ArchiveAssignmentService(assignmentRepository, courseRepository, courseMembershipRepository);
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
        public GetACourseService getACourseService(CourseRepository courseRepository,
                        CourseMembershipRepository courseMembershipRepository) {
                return new GetACourseService(courseRepository, courseMembershipRepository);
        }

        @Bean
        public GetCourseMembersService getMembersService(CourseMembershipRepository courseMembershipRepository,
                        UserRepository userRepository, CourseRepository courseRepository) {
                return new GetCourseMembersService(courseMembershipRepository, userRepository, courseRepository);
        }

        @Bean
        public CreateAssignmentService createAssignmentService(AssignmentRepository assignmentRepository,
                        CourseRepository courseRepository) {
                return new CreateAssignmentService(assignmentRepository, courseRepository);
        }

        @Bean
        public AddAssignmentArtifactService addReferenceArtifactService(
                        AssignmentArtifactRepository assignmentArtifactRepository,
                        AssignmentRepository assignmentRepository,
                        CourseRepository courseRepository, CourseMembershipRepository courseMembershipRepository) {
                return new AddAssignmentArtifactService(assignmentArtifactRepository, assignmentRepository,
                                courseRepository,
                                courseMembershipRepository);
        }

        @Bean
        public GetAssignmentArtifactsService getAssignmentArtifactsService(
                        AssignmentArtifactRepository assignmentArtifactRepository,
                        AssignmentRepository assignmentRepository,
                        CourseMembershipRepository courseMembershipRepository,
                        CourseRepository courseRepository) {
                return new GetAssignmentArtifactsService(assignmentRepository,
                                courseMembershipRepository, assignmentArtifactRepository, courseRepository);
        }

        @Bean
        public EditAssignmentService editAssignmentService(AssignmentRepository assignmentRepository,
                        CourseRepository courseRepository, CourseMembershipRepository courseMembershipRepository) {
                return new EditAssignmentService(assignmentRepository, courseRepository, courseMembershipRepository);
        }

        @Bean
        public EditAssignmentArtifactService editAssignmentArtifactService(
                        AssignmentArtifactRepository assignmentArtifactRepository,
                        AssignmentRepository assignmentRepository,
                        CourseRepository courseRepository,
                        CourseMembershipRepository courseMembershipRepository) {
                return new EditAssignmentArtifactService(assignmentArtifactRepository,
                                assignmentRepository, courseRepository, courseMembershipRepository);
        }

        @Bean
        public DeleteAssignmentArtifactService deleteAssignmentArtifactService(
                        AssignmentArtifactRepository assignmentArtifactRepository,
                        AssignmentRepository assignmentRepository,
                        CourseRepository courseRepository,
                        CourseMembershipRepository courseMembershipRepository) {
                return new DeleteAssignmentArtifactService(assignmentArtifactRepository,
                                assignmentRepository, courseRepository, courseMembershipRepository);
        }

        @Bean
        public CreateGroupService createGroupService(
                        AssignmentRepository assignmentRepository,
                        CourseMembershipRepository courseMembershipRepository,
                        GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository) {
                return new CreateGroupService(assignmentRepository, courseMembershipRepository,
                                groupRepository, groupMemberRepository);
        }

        @Bean
        public JoinOpenGroupService joinOpenGroupService(
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        GroupEntryRequestRepository groupEntryRequestRepository) {
                return new JoinOpenGroupService(courseMembershipRepository, assignmentRepository,
                                groupRepository, groupMemberRepository, groupEntryRequestRepository);
        }

        @Bean
        public RequestGroupEntryService requestGroupEntryService(
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        GroupEntryRequestRepository groupEntryRequestRepository) {
                return new RequestGroupEntryService(courseMembershipRepository, assignmentRepository,
                                groupRepository, groupMemberRepository, groupEntryRequestRepository);
        }

        @Bean
        public CancelGroupEntryRequestService cancelGroupEntryRequestService(
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupEntryRequestRepository groupEntryRequestRepository) {
                return new CancelGroupEntryRequestService(courseMembershipRepository, assignmentRepository,
                                groupRepository, groupEntryRequestRepository);
        }

        @Bean
        public GetUserGroupEntryRequestsService getUserGroupEntryRequestsService(
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository,
                        GroupEntryRequestRepository groupEntryRequestRepository) {
                return new GetUserGroupEntryRequestsService(courseMembershipRepository, assignmentRepository,
                                groupEntryRequestRepository);
        }

        @Bean
        public AcceptGroupEntryRequestService acceptGroupEntryRequestService(
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        GroupEntryRequestRepository groupEntryRequestRepository) {
                return new AcceptGroupEntryRequestService(courseMembershipRepository, assignmentRepository,
                                groupRepository, groupMemberRepository, groupEntryRequestRepository);
        }

        @Bean
        public RejectGroupEntryRequestService rejectGroupEntryRequestService(
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupEntryRequestRepository groupEntryRequestRepository) {
                return new RejectGroupEntryRequestService(courseMembershipRepository, assignmentRepository,
                                groupRepository, groupEntryRequestRepository);
        }

        @Bean
        public GetGroupEntryRequestsService getGroupEntryRequestsService(
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupEntryRequestRepository groupEntryRequestRepository) {
                return new GetGroupEntryRequestsService(courseMembershipRepository, assignmentRepository,
                                groupRepository, groupEntryRequestRepository);
        }

        @Bean
        public ChangeGroupModeService changeGroupModeService(
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupEntryRequestRepository groupEntryRequestRepository) {
                return new ChangeGroupModeService(courseMembershipRepository, assignmentRepository,
                                groupRepository, groupEntryRequestRepository);
        }

        @Bean
        public DissolveGroupService dissolveGroupService(
                        CourseRepository courseRepository,
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository) {
                return new DissolveGroupService(courseRepository, courseMembershipRepository, assignmentRepository,
                                groupRepository);
        }

        @Bean
        public RemoveGroupMemberService removeGroupMemberService(
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository) {
                return new RemoveGroupMemberService(courseMembershipRepository, assignmentRepository,
                                groupRepository, groupMemberRepository);
        }

        @Bean
        public LeaveGroupService leaveGroupService(
                        GroupMemberRepository groupMemberRepository,
                        GroupRepository groupRepository,
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository) {
                return new LeaveGroupService(groupMemberRepository, groupRepository,
                                courseMembershipRepository, assignmentRepository);
        }

        @Bean
        public GetAssignmentGroupsService getAssignmentGroupsService(
                        CourseRepository courseRepository,
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository) {
                return new GetAssignmentGroupsService(courseRepository, courseMembershipRepository,
                                assignmentRepository, groupRepository, groupMemberRepository);
        }

        @Bean
        public AddGroupArtifactService addGroupArtifactService(
                        CourseMembershipRepository courseMembershipRepository,
                        CourseRepository courseRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        GroupArtifactRepository groupArtifactRepository) {
                return new AddGroupArtifactService(
                                courseMembershipRepository,
                                courseRepository,
                                assignmentRepository,
                                groupRepository,
                                groupMemberRepository,
                                groupArtifactRepository);
        }

        @Bean
        public GetGroupArtifactsService getGroupArtifactsService(
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        GroupArtifactRepository groupArtifactRepository) {
                return new GetGroupArtifactsService(
                                courseMembershipRepository,
                                assignmentRepository,
                                groupRepository,
                                groupMemberRepository,
                                groupArtifactRepository);
        }

        @Bean
        public GetGroupMembersService getGroupMembersService(
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        UserRepository userRepository) {
                return new GetGroupMembersService(
                                courseMembershipRepository,
                                assignmentRepository,
                                groupRepository,
                                groupMemberRepository,
                                userRepository);
        }

        @Bean
        public GetPublicGroupArtifactsService getPublicGroupArtifactsService(
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupArtifactRepository groupArtifactRepository) {
                return new GetPublicGroupArtifactsService(
                                courseMembershipRepository,
                                assignmentRepository,
                                groupRepository,
                                groupArtifactRepository);
        }

        @Bean
        public EditGroupArtifactService editGroupArtifactService(
                        CourseMembershipRepository courseMembershipRepository,
                        CourseRepository courseRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        GroupArtifactRepository groupArtifactRepository) {
                return new EditGroupArtifactService(
                                courseMembershipRepository,
                                courseRepository,
                                assignmentRepository,
                                groupRepository,
                                groupMemberRepository,
                                groupArtifactRepository);
        }

        @Bean
        public ChangeGroupArtifactPrivacyService changeGroupArtifactPrivacyService(
                        CourseMembershipRepository courseMembershipRepository,
                        CourseRepository courseRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        GroupArtifactRepository groupArtifactRepository) {
                return new ChangeGroupArtifactPrivacyService(
                                courseMembershipRepository,
                                courseRepository,
                                assignmentRepository,
                                groupRepository,
                                groupMemberRepository,
                                groupArtifactRepository);
        }

        @Bean
        public DeleteGroupArtifactService deleteGroupArtifactService(
                        CourseMembershipRepository courseMembershipRepository,
                        CourseRepository courseRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        GroupArtifactRepository groupArtifactRepository) {
                return new DeleteGroupArtifactService(
                                courseMembershipRepository,
                                courseRepository,
                                assignmentRepository,
                                groupRepository,
                                groupMemberRepository,
                                groupArtifactRepository);
        }

        @Bean
        public ChangeGroupArtifactDeliverableStatusService changeGroupArtifactDeliverableStatusService(
                        CourseRepository courseRepository,
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        GroupArtifactRepository groupArtifactRepository) {
                return new ChangeGroupArtifactDeliverableStatusService(
                                courseRepository,
                                courseMembershipRepository,
                                assignmentRepository,
                                groupRepository,
                                groupMemberRepository,
                                groupArtifactRepository);
        }

        @Bean
        public EditGroupService editGroupService(
                        CourseRepository courseRepository,
                        CourseMembershipRepository courseMembershipRepository,
                        AssignmentRepository assignmentRepository,
                        GroupRepository groupRepository) {
                return new EditGroupService(courseRepository, courseMembershipRepository,
                                assignmentRepository, groupRepository);
        }
}
