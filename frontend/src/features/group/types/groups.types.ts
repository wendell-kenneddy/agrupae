export interface Group {
  id: string
  assignmentId: string
  leaderId: string
  name: string
  open: boolean
  membersCanEditArtifacts: boolean
  createdAt: string
  updatedAt: string
}

export interface CreateGroupRequest {
  name: string
  open: boolean
}

export interface GroupSummary {
  id: string
  assignmentId: string
  leaderId: string
  name: string
  open: boolean
  membersCanEditArtifacts: boolean
  memberCount: number
  createdAt: string
  updatedAt: string
}

export interface AssignmentGroupsResponse {
  myGroup: GroupSummary | null
  groups: {
    content: GroupSummary[]
    totalPages: number
    totalElements: number
    size: number
    number: number
  }
}

export type GroupEntryRequestStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED'

export interface GroupEntryRequest {
  id: string
  groupId: string
  userId: string
  status: GroupEntryRequestStatus
  createdAt: string
  updatedAt: string
}
