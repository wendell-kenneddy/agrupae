import api from '@/lib/axios'
import type {
  Group,
  CreateGroupRequest,
  AssignmentGroupsResponse,
  GroupEntryRequest,
  GroupEntryRequestStatus,
} from '@/features/group/types/groups.types'

export async function createGroup(
  courseId: string,
  assignmentId: string,
  data: CreateGroupRequest
): Promise<Group> {
  const response = await api.post<Group>(
    `/courses/${courseId}/assignments/${assignmentId}/groups`,
    data
  )
  return response.data
}

export async function getGroups(
  courseId: string,
  assignmentId: string
): Promise<AssignmentGroupsResponse> {
  const response = await api.get<AssignmentGroupsResponse>(
    `/courses/${courseId}/assignments/${assignmentId}/groups`
  )
  return response.data
}

export async function joinOpenGroup(
  courseId: string,
  assignmentId: string,
  groupId: string
): Promise<void> {
  await api.post(`/courses/${courseId}/assignments/${assignmentId}/groups/${groupId}/join`)
}

export async function requestGroupEntry(
  courseId: string,
  assignmentId: string,
  groupId: string
): Promise<GroupEntryRequest> {
  const response = await api.post<GroupEntryRequest>(
    `/courses/${courseId}/assignments/${assignmentId}/groups/${groupId}/entry-requests`
  )
  return response.data
}

export async function cancelGroupEntryRequest(
  courseId: string,
  assignmentId: string,
  groupId: string,
  requestId: string
): Promise<void> {
  await api.delete(
    `/courses/${courseId}/assignments/${assignmentId}/groups/${groupId}/entry-requests/${requestId}`
  )
}

export async function getMyEntryRequests(
  courseId: string,
  assignmentId: string
): Promise<GroupEntryRequest[]> {
  const response = await api.get<GroupEntryRequest[]>(
    `/courses/${courseId}/assignments/${assignmentId}/entry-requests/me`
  )
  return response.data
}

export async function getGroupEntryRequests(
  courseId: string,
  assignmentId: string,
  groupId: string,
  status?: GroupEntryRequestStatus
): Promise<GroupEntryRequest[]> {
  const response = await api.get<GroupEntryRequest[]>(
    `/courses/${courseId}/assignments/${assignmentId}/groups/${groupId}/entry-requests`,
    { params: status ? { status } : {} }
  )
  return response.data
}

export async function acceptGroupEntryRequest(
  courseId: string,
  assignmentId: string,
  groupId: string,
  requestId: string
): Promise<void> {
  await api.post(
    `/courses/${courseId}/assignments/${assignmentId}/groups/${groupId}/entry-requests/${requestId}/accept`
  )
}

export async function rejectGroupEntryRequest(
  courseId: string,
  assignmentId: string,
  groupId: string,
  requestId: string
): Promise<GroupEntryRequest> {
  const response = await api.post<GroupEntryRequest>(
    `/courses/${courseId}/assignments/${assignmentId}/groups/${groupId}/entry-requests/${requestId}/reject`
  )
  return response.data
}

export async function changeGroupMode(
  courseId: string,
  assignmentId: string,
  groupId: string,
  open: boolean
): Promise<void> {
  await api.put(`/courses/${courseId}/assignments/${assignmentId}/groups/${groupId}/mode`, {
    open,
  })
}

export async function dissolveGroup(
  courseId: string,
  assignmentId: string,
  groupId: string
): Promise<void> {
  await api.delete(`/courses/${courseId}/assignments/${assignmentId}/groups/${groupId}`)
}



