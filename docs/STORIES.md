# Agrupaê
## User Stories

**Based on Requirements Document v1.6 — Revised MVP · 2025**

> Format: *As a [role], I want to [action] so that [benefit].*
> Roles: **User** (any authenticated user), **Student** (class member), **Class Leader** (class creator), **Group Leader** (group leader), **Admin**.

---

## Epic 1 — Authentication and Profile

### US-01 — Registration
**Requirements:** RF01, RNF02A

As a user, I want to register with my email and password, so that I can access the platform.

**Acceptance criteria:**
- Email serves as the unique identifier in the system.
- Password is stored as a bcrypt hash (12 rounds).
- Attempting to register with an already-used email returns a clear error.

---

### US-02 — Login
**Requirements:** RF02, RNF02B

As a user, I want to log in with my email and password, so that I can access my account securely.

**Acceptance criteria:**
- Valid login returns an access token (JWT, max 30 min lifespan) in the response body and a refresh token (JWT, max 7 days lifespan) via a secure HTTP-only cookie.
- The access token is stored in-memory only on the client; never in localStorage or sessionStorage.
- Invalid login displays an error message without revealing which field is incorrect.

---

### US-03 — Session Management and Automatic Refresh
**Requirements:** RF03, RF04, RNF02B

As a user, I want my session to be automatically maintained while I am active, so that I do not need to log in again during use.

**Acceptance criteria:**
- Sessions are managed through an access token and a refresh token pair.
- When the access token expires, the system automatically uses the refresh token to issue a new token pair.
- Upon successful refresh, the new token pair is sent to the client under the same transport rules (access token via body, refresh token via secure cookie).
- The user is not interrupted during automatic refresh.

---

### US-04 — Logout
**Requirements:** RF05

As a user, I want to log out, so that my session is fully terminated and my account stays secure.

**Acceptance criteria:**
- Logout invalidates the entire family of refresh tokens issued since the last login.
- After logout, any attempt to use an existing refresh token from that family is rejected.

---

### US-05 — Protection Against Stolen Refresh Tokens
**Requirements:** RF06

As the system, I need to detect and respond to refresh token reuse, so that stolen tokens cannot be exploited.

**Acceptance criteria:**
- If a client sends a refresh request with an already-revoked refresh token, the system immediately invalidates all refresh tokens in that token's family.
- The user is effectively logged out from all sessions as a result.

---

### US-06 — Profile Management
**Requirements:** RF07

As a user, I want to view and edit my profile, so that my information is accurate and up to date.

**Acceptance criteria:**
- Users can edit their name, email, and skill tags at any time.
- Skill tags are visible to other students in the "students without a group" listing.

---

### US-07 — User List for Class Leaders
**Requirements:** RF08

As a class leader, I want to view a list of all users enrolled in my class, so that I can select someone to transfer my class responsibility to.

**Acceptance criteria:**
- The list is accessible only to the class leader and admins.
- The list shows all users currently enrolled in that class.

---

### US-08 — User List for Admins
**Requirements:** RF09

As an admin, I want to view a list of all users in the system, so that I can manage accounts effectively.

**Acceptance criteria:**
- The full user list is accessible only to admins.
- Admins can use this list for management purposes across the system.

---

## Epic 2 — Classes

### US-09 — Class Creation
**Requirements:** RF11, RF12

As a user, I want to create a class, so that I can organize students around a course or activity.

**Acceptance criteria:**
- Any authenticated user can create a class by providing a name.
- Upon creation, the user automatically becomes the class leader with full management powers over it.
- A unique invitation code is automatically generated and displayed to the class leader.
- An admin who creates a class assumes the same leader role.

---

### US-10 — Joining a Class
**Requirements:** RF13, RF10

As a user, I want to join a class using an invitation code, so that I can access its assignments and groups.

**Acceptance criteria:**
- A valid code adds the user to the class exclusively as a student, with no management powers.
- An invalid code displays a clear error message.
- The class appears in the user's list after joining.
- Users who join via code do not receive management powers, even if they are a class leader in other classes or a system admin.

---

### US-11 — Class Archival
**Requirements:** RF14

As a class leader, I want to archive my class at the end of a period, so that all assignments and groups are frozen.

**Acceptance criteria:**
- Only the class leader or an admin can archive the class.
- Upon archival, all assignments and associated groups are automatically frozen.
- No changes can be made to archived classes.

---

### US-12 — Class Visibility
**Requirements:** RF15

As a user, I want to see only the classes I belong to, so that my dashboard stays relevant and uncluttered.

**Acceptance criteria:**
- A class is only visible to a user after they have joined it via invitation code or created it.
- Classes not joined are not discoverable.

---

### US-13 — Transfer of Class Responsibility
**Requirements:** RF16

As a class leader, I want to transfer my class responsibility to another enrolled user, so that someone else can manage it in my place.

**Acceptance criteria:**
- The class leader can transfer responsibility to any user currently enrolled in the class, regardless of that user's global role.
- After the transfer, the previous leader loses all management powers.
- If the previous leader was enrolled in the class as a student, they remain as a student.
- An admin can perform the transfer without the current leader's consent.

---

## Epic 3 — Assignments

### US-14 — Assignment Creation
**Requirements:** RF17, RF22, RNF17A

As a class leader, I want to create an assignment with configurable group-formation rules, so that I can structure teamwork according to the activity's needs.

**Acceptance criteria:**
- The class leader defines: name, optional description, optional due date, member limit per group (or unlimited), and flag configuration.
- The interface offers the **Free**, **Moderate**, and **Controlled** presets as a starting point, visually distinct from custom selections.
- Individual flags can be adjusted after choosing a preset.
- Combinations classified as **Invalid** (Section 2.3 of the requirements) are blocked with an error message before saving.
- Combinations classified as **Warning** display a warning dialog and require explicit user confirmation before saving.
- A confirmation dialog is also displayed when saving a custom flag combination that does not match any preset.

---

### US-15 — Reference Artifacts
**Requirements:** RF18

As a class leader, I want to add reference artifacts to an assignment, so that students have all necessary materials in one place.

**Acceptance criteria:**
- Artifacts added by the class leader are visible to all students in the class.
- Students cannot add artifacts to the assignment; they can only add artifacts to their own group.

---

### US-16 — Assignment Archival
**Requirements:** RF19

As a class leader, I want to archive an assignment individually, so that I can close it without archiving the entire class.

**Acceptance criteria:**
- Only the class leader or an admin can archive an assignment.
- When the class is archived, all assignments are automatically archived.
- Archived assignments are frozen: no changes to groups are permitted.

---

### US-17 — Monitoring Dashboard
**Requirements:** RF20, RNF20A

As a class leader, I want to view a dashboard with all groups and ungrouped students per assignment, so that I can monitor team formation progress.

**Acceptance criteria:**
- The dashboard displays group name, current members, and available spots for each group.
- It also lists students who do not yet belong to any group in the assignment.
- Data is refreshed on each page load and loads within 2 seconds.
- Visible only to the class leader and admins.

---

### US-18 — Assignment Editing
**Requirements:** RF21, RF22, RNF21A

As a class leader, I want to edit an assignment after its creation, so that I can adjust rules as circumstances change.

**Acceptance criteria:**
- Only the class leader or an admin can edit any assignment in the class, regardless of who created it.
- Flag editing is permitted while the assignment is active.
- The same validation rules from assignment creation apply to editing (invalid combinations blocked, warning combinations require confirmation).
- Flag validation is performed both client-side and server-side; server-side validation is final.

---

### US-19 — Pre-Creation of Groups by Class Leader
**Requirements:** RF23, RF24, RF25

As a class leader, I want to pre-create groups for an assignment, so that team structure is defined before students interact.

**Acceptance criteria:**
- Available when `studentsCanCreateGroups = N`.
- Groups can be created empty (for students to organize themselves) or with composition already defined, when `classLeaderCanEditComposition = Y`.
- When `classLeaderCanEditComposition = Y`, the class leader designates a group leader upon creation and can reassign leadership at any time while the assignment is active.
- The class leader can add or remove students from any group at any time while the assignment is within the deadline.
- The member limit defined in the assignment also applies to these operations; to exceed it, the class leader must first edit the assignment's member limit.

---

## Epic 4 — Groups

### US-20 — Creating a Group
**Requirements:** RF26, RF32

As a student, I want to create a group in an assignment, so that I can gather classmates and collaborate.

**Acceptance criteria:**
- Available when `studentsCanCreateGroups = Y`.
- The student defines the group name and mode (open or closed).
- The creator automatically becomes the group leader.
- Blocked if the student already belongs to another group in the same assignment.

---

### US-21 — Joining an Open Group
**Requirements:** RF27, RF31, RF32

As a student, I want to join an open group directly, so that I can become part of a team without requiring approval.

**Acceptance criteria:**
- Entry is permitted if spots are available.
- Entry is blocked if the group is full.
- Blocked if the student already belongs to another group in the same assignment.

---

### US-22 — Requesting Entry into a Closed Group
**Requirements:** RF28, RF29, RF32, RNF28A

As a student, I want to request entry into a closed group, so that the group leader can review and decide on my admission.

**Acceptance criteria:**
- Only one pending request per assignment per student at a time.
- The student can cancel the request at any time.
- If the group becomes full before the leader responds, the request is automatically cancelled.
- Rejected requests are shown with a "rejected" status in the student's interface.
- The student has a distinct visual indicator for each request status: pending, accepted, or rejected.
- Blocked if the student already belongs to another group in the same assignment.

---

### US-23 — Managing Entry Requests
**Requirements:** RF28, RF29, RF36

As a group leader of a closed group, I want to accept or reject membership requests, so that I can control my group's composition.

**Acceptance criteria:**
- The leader sees the list of pending requests.
- Upon accepting, the student joins if spots are still available; if the group is now full, the acceptance is blocked.
- Upon rejecting, the request appears with "rejected" status in the student's interface. No active notification is sent.
- This action is always available to the group leader of a closed group, independent of other flags.

---

### US-24 — Toggling Group Mode
**Requirements:** RF30

As a group leader, I want to toggle my group between open and closed modes, so that I can control how new members join.

**Acceptance criteria:**
- Available when `groupLeaderCanToggleMode = Y`.
- Mode can be toggled at any time while the assignment is within the deadline.

---

### US-25 — Removing a Member
**Requirements:** RF36

As a group leader, I want to remove a member from the group, so that I can adjust the team composition when needed.

**Acceptance criteria:**
- Available when `groupLeaderCanRemoveMembers = Y`.
- The removed member loses their group affiliation and can join another group.
- The leader cannot remove themselves via this action; they must use the leave group action instead.

---

### US-26 — Leaving a Group
**Requirements:** RF33, RF35

As a student, I want to leave a group, so that I can join another or remain without a group.

**Acceptance criteria:**
- Available when `studentCanLeaveGroups = Y` and the assignment is within the deadline.
- Blocked when `studentCanLeaveGroups = N`.
- If the group leader leaves, leadership is automatically transferred to the longest-standing member, who inherits the same powers defined by the assignment flags.
- When `studentCanLeaveGroups = N`, automatic leadership succession does not apply.

---

### US-27 — Dissolving a Group
**Requirements:** RF34

As a group leader, I want to dissolve my group, so that all members are freed and can form new teams.

**Acceptance criteria:**
- Available when `groupLeaderCanDissolveGroup = Y`.
- Upon dissolution, all members lose their group affiliation and can join or form another group.
- When `groupLeaderCanDissolveGroup = N` and `classLeaderCanEditComposition = Y`, only the class leader can dissolve the group.

---

### US-28 — Group Internal Artifacts
**Requirements:** RF37

As a group member, I want to add useful artifacts to my group, so that the team's resources are centralized in one place.

**Acceptance criteria:**
- Any group member can add internal artifacts.
- Internal artifacts are visible only to members of that group.
- They are not visible to students outside the group or to the class leader via the monitoring dashboard.
- Visibility of each artifact is managed by the group members.

---

## Epic 5 — Discovery and Visibility

### US-29 — Viewing Available Groups
**Requirements:** RF38, RNF38A

As a student, I want to see all groups in an assignment with their members and available spots, so that I can decide which one to join.

**Acceptance criteria:**
- The listing displays group name, current members, and remaining spots.
- Visible to all students in the class.
- Internal artifacts of groups are not displayed in the listing.
- The listing loads within 1 second.

---

### US-30 — Viewing Students Without a Group
**Requirements:** RF39

As a student, I want to see which classmates do not yet have a group, so that I can reach out to those also looking for a team.

**Acceptance criteria:**
- The list displays the name and skill tags of students without a group.
- Updated on each page load.
- Includes any user enrolled in the class as a student who does not yet belong to a group in that assignment.
- Users who are class leaders in other classes appear normally in this list when enrolled as a student.

---

### US-31 — Class Leader Visual Distinction
**Requirements:** RF40

As a student, I want to visually distinguish the class leader from other students in the interface, so that I know who manages the class.

**Acceptance criteria:**
- The class leader is visually differentiated from regular students in all relevant user interfaces (e.g., member lists, group listings, student listings).

---

*Agrupaê — User Stories · Based on Requirements v1.6 · 2025*
