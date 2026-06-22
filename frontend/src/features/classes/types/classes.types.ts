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
  memberCount: number
  activeAssignments?: number
  role: ClassRole
  previewMembers?: ClassMemberPreview[]
}

export interface CreateClassRequest {
  name: string
  description?: string
}

export interface JoinClassRequest {
  inviteCode: string
}

export interface Member {
  id: string
  name: string
  email: string
  role: 'TEACHER' | 'STUDENT'
  createdAt: string
  updatedAt: string
}

export interface LeadershipTransferRequest {
  id: string
  courseId: string
  senderId: string
  senderName: string
  targetId: string
  targetName: string
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED'
  createdAt: string
  updatedAt: string
}
