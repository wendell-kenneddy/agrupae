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
