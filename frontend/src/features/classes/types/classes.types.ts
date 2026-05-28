export type ClassRole = 'OWNER' | 'STUDENT'

export interface ClassMemberPreview {
  id: string
  name: string
  avatarUrl?: string
}

export interface Class {
  id: string
  leaderId: string
  name: string
  description?: string
  inviteCode: string
  archived: boolean
  createdAt: string
  updatedAt: string
  // memberCount: number
  // activeAssignments: number
  // role: ClassRole
  // previewMembers: ClassMemberPreview[]
}

export interface CreateClassRequest {
  name: string
  description?: string
}

export interface JoinClassRequest {
  inviteCode: string
}
