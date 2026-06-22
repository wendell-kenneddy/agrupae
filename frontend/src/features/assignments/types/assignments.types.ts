export interface AssignmentFlags {
  maxGroupMembers: number
  maxGroups: number
  studentsCanCreateGroups: boolean
  studentsCanLeaveGroups: boolean
  groupLeaderCanDissolve: boolean
  groupLeaderCanRemoveMembers: boolean
  groupLeaderCanChangeMode: boolean
  groupLeaderCanTransferLeadership: boolean
  supervisorCanEditGroups: boolean
}

export type AssignmentMode = 'free' | 'moderate' | 'controlled' | 'advanced'

export interface Assignment {
  id: string
  courseId: string
  name: string
  description?: string
  assignmentFlags: AssignmentFlags
  isArchived: boolean
  dueDate?: string
  createdAt: string
  updatedAt: string
}

export interface CreateAssignmentRequest {
  name: string
  description?: string
  dueDate?: string
  assignmentFlags: AssignmentFlags
}

export const PRESETS: Record<
  Exclude<AssignmentMode, 'advanced'>,
  Omit<AssignmentFlags, 'maxGroupMembers' | 'maxGroups'>
> = {
  free: {
    studentsCanCreateGroups: true,
    studentsCanLeaveGroups: true,
    groupLeaderCanDissolve: true,
    groupLeaderCanRemoveMembers: true,
    groupLeaderCanChangeMode: true,
    groupLeaderCanTransferLeadership: true,
    supervisorCanEditGroups: false,
  },
  moderate: {
    studentsCanCreateGroups: false,
    studentsCanLeaveGroups: true,
    groupLeaderCanDissolve: false,
    groupLeaderCanRemoveMembers: true,
    groupLeaderCanChangeMode: true,
    groupLeaderCanTransferLeadership: true,
    supervisorCanEditGroups: false,
  },
  controlled: {
    studentsCanCreateGroups: false,
    studentsCanLeaveGroups: false,
    groupLeaderCanDissolve: false,
    groupLeaderCanRemoveMembers: false,
    groupLeaderCanChangeMode: false,
    groupLeaderCanTransferLeadership: false,
    supervisorCanEditGroups: true,
  },
}

export interface AssignmentArtifact {
  id: string
  assignmentId: string
  name: string
  description: string
  resourceLink: string
  required: boolean
  createdAt: string
  updatedAt: string
}

export interface AddArtifactRequest {
  name: string
  description: string
  resourceLink: string
  required?: boolean
}
