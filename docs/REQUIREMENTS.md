# Agrupaê

## 1. System Roles

The system features two global roles and one contextual role per class:

- **Admin** — full system access, including user management and operations on any class. Initially created via seed in the database.
- **Student** — standard access. Can create classes, enroll in classes via invitation code, and participate in groups. Upon creating a class, automatically assumes the role of class leader.
- **Class Leader** *(contextual role)* — granted to whoever creates the class. Allows creating and managing assignments, viewing the monitoring dashboard, archiving the class, and transferring responsibility. A user can be the leader of multiple classes and simultaneously a student in others.

---

## 2. Assignment Configuration Model

When creating an assignment, the class leader or admin defines its behavior through a set of **independent configuration flags**. To facilitate use, the interface offers **named presets** that populate the flags automatically. After choosing a preset, individual flags can be adjusted, provided the resulting combination is valid.

### 2.1 Configuration Flags

| Flag | Description |
|---|---|
| `studentsCanCreateGroups` | Allows students to create new groups within the assignment. |
| `studentCanLeaveGroups` | Allows students to voluntarily leave a group. |
| `groupLeaderCanDissolveGroup` | Allows the leader to dissolve the group, freeing all members. |
| `groupLeaderCanRemoveMembers` | Allows the leader to remove individual members from the group. |
| `groupLeaderCanToggleMode` | Allows the leader to toggle the group mode between open and closed. |
| `classLeaderCanEditComposition` | Allows the class leader to edit the composition of any group at any time while the assignment is within the deadline. Also enables pre-creating groups with students already defined. |

### 2.2 Configuration Presets

Y = enabled · N = disabled

| Preset | create groups | can leave | leader dissolves | leader removes | toggle mode | class leader edits |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| **Free** — full student autonomy | Y | Y | Y | Y | Y | N |
| **Moderate** — class leader structures, students join | N | Y | N | Y | Y | N |
| **Controlled** — class leader defines and maintains composition | N | N | N | N | N | Y |

> Beyond the presets, custom combinations are valid as long as they respect the constraints in Section 2.3. The interface must display explicit confirmation when saving combinations that don't match any preset.

### 2.3 Combination Constraints

| Severity | Invalid Combination | Reason |
|---|---|---|
| **Invalid** | `studentsCanCreateGroups = N` and `classLeaderCanEditComposition = N` | No actor can create groups. The assignment becomes unusable. The system must reject this combination before persisting. |
| **Invalid** | `studentCanLeaveGroups = N` and `groupLeaderCanDissolveGroup = Y` | Dissolution is a forced collective exit. Contradicts the voluntary exit restriction. The system must reject this combination before persisting. |
| **Warning** | `studentsCanCreateGroups = Y` and `classLeaderCanEditComposition = Y` and `studentCanLeaveGroups = N` | Authority over composition is ambiguous between students and instructor. Probably a configuration mistake. The interface must require explicit confirmation. |
| **Warning** | `groupLeaderCanDissolveGroup = Y` and `studentsCanCreateGroups = N` and `classLeaderCanEditComposition = N` | The leader can dissolve, but no group can be created again. Dissolved members remain permanently without a group. The interface must require explicit confirmation. |

---

## 4. Leadership Rules

- The current group leader has editing and management powers over the group according to the flags enabled in the assignment.
- In the **Free** preset: the group creator automatically becomes the leader.
- In the **Moderate** preset: the first student to join becomes the leader.
- In the **Controlled** preset: the class leader designates the leader when creating the group.
- When `studentCanLeaveGroups = Y`: if the leader leaves the group, leadership is automatically transferred to the oldest member, with the same powers defined by the flags.
- When `studentCanLeaveGroups = N`: automatic succession does not apply, since the leader cannot leave.
- When `classLeaderCanEditComposition = Y`: the class leader can reassign leadership directly at any time.

---

## 5. Functional Requirements

> Items marked with `*` indicate secondary priority requirements within the MVP.

### 5.1 Authentication and Users

| ID | Name | Description |
|---|---|---|
| RF01 | User registration | New users can register with email and password. The email serves as unique identifier. |
| RF02 | User login | Registered users can log in with email and password. Sessions are managed via JWT tokens with refresh mechanism. |
| RF03 | Profile management | Users can view and edit their profile, including name, email, and skill tags. |
| RF04 | User list for class leaders | Class leaders can view a list of all users in class for transfer of responsibility purposes. |
| RF05 | User list for admins | Admins can view a list of all users in the system for management purposes. |
| RF06 | Role assignment at entry | When joining a class via invitation code, the user receives only the **Student** role in that class, regardless of their global role. A user can be admin in the system, leader of one class, and student in another simultaneously. |

### 5.2 Classes

| ID | Name | Description |
|---|---|---|
| RF07  | Class creation | Any authenticated user can create a class with a name. Upon creating it, they automatically become its **leader**, acquiring exclusive management powers over it. An admin who creates a class assumes the same leader role. |
| RF08 | Invitation code | Each class has a unique auto-generated invitation code, shareable by the leader or admin. |
| RF09  | Entry via code | Any user enters a class by inserting the invitation code, assuming exclusively the **Student** role in that class — with no management powers — regardless of being leader for other classes or admin. |
| RF10  | Class archival | Only the class leader or an admin can archive it. Archival freezes all assignments and associated groups. |
| RF11 | Class visibility | A class is only visible to the user after they join it via invitation code or have created it. |
| RF38  | Class responsibility transfer | The leader can transfer their responsibility over the class to any other user registered in the system, as long as the targeted user is enrolled in the class, regardless of that user's global role. After the transfer, the previous leader loses all management powers and becomes a student in the class, if already joined via code. The admin can perform the transfer without consent from the current leader. |

### 5.3 Assignments

| ID | Name | Description |
|---|---|---|
| RF12  | Assignment creation | The class leader or admin can create assignments within a class, defining: name, description, due date, member limit per group (or unlimited), and flag configuration (via preset or custom). The interface offers Free, Moderate, and Controlled presets as a starting point, with the possibility of adjusting each flag individually. |
| RF13  | Reference artifacts | The class leader or admin can add reference artifacts to the assignment, visible to all students in the class. |
| RF14  | Assignment archival | Only the class leader or an admin can archive an assignment individually. When archiving the class, all assignments are automatically archived. |
| RF15  | Monitoring dashboard | The class leader and admins view, per assignment, all formed groups, number of members, and students still without a group. Data updated on each page load. |
| RF16  | Assignment editing | Only the class leader or an admin can edit any assignment in the class, regardless of who created it. Flag editing is allowed while the assignment is active, respecting the constraints in Section 3.3. |
| RF35 | Flag validation on save | The system validates the flag combination when creating or editing an assignment, before persisting. Combinations classified as **Invalid** (Section 2.3) return an error and block saving. Combinations classified as **Warning** display a warning and require explicit user confirmation. |
| RF36 | Pre-creation of groups by class leader | When `studentsCanCreateGroups = N`, the class leader must pre-create the assignment groups before students can interact. Groups can be created empty (for students to organize) or with composition already defined, according to `classLeaderCanEditComposition`. |
| RF37 | Leader designation by class leader | When `classLeaderCanEditComposition = Y`, the class leader designates a leader when creating the group and can reassign leadership at any time while the assignment is active. |
| RF17  | Composition editing by class leader | When `classLeaderCanEditComposition = Y`, the class leader can add or remove students from any group at any time while the assignment is within the deadline. The member limit defined in the assignment also applies to these operations; to exceed it, the class leader must edit the assignment limit before adjusting the composition. |

### 5.4 Groups

| ID | Name | Description |
|---|---|---|
| RF19  | Group creation by student | When `studentsCanCreateGroups = Y`, students can create groups within an assignment, defining name and mode (open or closed). The creator automatically becomes the leader. A student cannot create a group if they already belong to another in the same assignment. |
| RF20 | Open group | In open groups, any student in the class can join directly, as long as there are spots available. |
| RF21 | Closed group — request | In closed groups, the student requests entry. The leader accepts or rejects. Only one pending request at a time per student per assignment. |
| RF22 | Automatic cancellation of requests | If a group becomes full before the leader responds, pending requests are automatically canceled. Rejected requests retain visible status to the student in the interface; no active notifications are sent. |
| RF23  | Mode toggle by leader | When `groupLeaderCanToggleMode = Y`, the leader can toggle the group mode between open and closed at any time while the assignment is within the deadline. |
| RF24 | Member limit | No group can exceed the member limit defined in the assignment. The rule applies to everyone, including the class leader when editing composition. To exceed the limit, the class leader must edit the assignment limit before adjusting the group. |
| RF25 | One group per assignment | A student can participate in only one group per assignment. |
| RF26  | Leaving the group | When `studentCanLeaveGroups = Y`, the student can leave the group at any time while the assignment is within the deadline. When `studentCanLeaveGroups = N`, voluntary exit is blocked. |
| RF27  | Group dissolution | When `groupLeaderCanDissolveGroup = Y`, the leader can dissolve the group. When `classLeaderCanEditComposition = Y` and `groupLeaderCanDissolveGroup = N`, only the class leader can dissolve. Upon dissolution, all members become without a group. |
| RF28  | Leadership succession | When `studentCanLeaveGroups = Y` and the leader leaves the group, leadership is automatically transferred to the oldest member, with powers defined by the assignment flags. When `studentCanLeaveGroups = N`, this rule does not apply. |
| RF29  | Leader management powers | The leader can: edit the group name (always); manage entry requests in closed groups (always); remove members when `groupLeaderCanRemoveMembers = Y`; toggle mode when `groupLeaderCanToggleMode = Y`; dissolve the group when `groupLeaderCanDissolveGroup = Y`. Each power is individually verified against the corresponding flag in the assignment. |
| RF30 | Group internal artifacts | Group members can add useful artifacts to the group. The visibility of the artifact is managed by the group members. |

### 5.5 Visibility and Discovery

| ID | Name | Description |
|---|---|---|
| RF31 | Public group listing | All students in a class can see all groups in an assignment, with name, members, and available spots. |
| RF32  | List of students without group | All students in the class can see which classmates do not yet belong to any group in the assignment. Users enrolled as students in classes they don't instruct appear in this list normally. |

