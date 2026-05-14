export type ClassRole = 'OWNER' | 'STUDENT'

export interface Class {
  id: string
  name: string
  memberCount: number
  activeAssignments: number
  role: ClassRole
  inviteCode: string
  previewMembers: ClassMemberPreview[]
}

export interface ClassMemberPreview {
  id: string
  name: string
  avatarUrl?: string
}

export interface Member {
  id: string
  name: string
  email: string
  avatarUrl?: string
}

export interface Assignment {
  id: string
  name: string
  deadline: string
  groupsFormed: number
  totalGroups: number
}
