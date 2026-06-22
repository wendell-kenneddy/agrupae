import api from '@/lib/axios'
import type {
  Class,
  CreateClassRequest,
  JoinClassRequest,
  Member,
  LeadershipTransferRequest,
} from '@/features/classes/types/classes.types'

export async function createClass(data: CreateClassRequest): Promise<Class> {
  const response = await api.post<Class>('/courses', data)
  return response.data
}

export async function joinClass(data: JoinClassRequest): Promise<Class> {
  const response = await api.post<Class>('/courses/join', data)
  return response.data
}

export async function archiveClass(id: string): Promise<void> {
  await api.post(`/courses/${id}/archive`)
}

export async function getCourses(): Promise<Class[]> {
  const response = await api.get<{ content: Class[] }>('/courses')
  return response.data.content
}

export async function transferOwnership(courseId: string, newLeaderId: string): Promise<LeadershipTransferRequest> {
  const response = await api.post<LeadershipTransferRequest>(`/courses/${courseId}/transfer`, { newLeaderId })
  return response.data
}

export async function getPendingTransferRequest(courseId: string): Promise<LeadershipTransferRequest | null> {
  const response = await api.get<LeadershipTransferRequest | null>(`/courses/${courseId}/transfer/request`)
  return response.status === 204 ? null : response.data
}

export async function acceptTransferRequest(courseId: string, requestId: string): Promise<void> {
  await api.post(`/courses/${courseId}/transfer/requests/${requestId}/accept`)
}

export async function rejectTransferRequest(courseId: string, requestId: string): Promise<void> {
  await api.post(`/courses/${courseId}/transfer/requests/${requestId}/reject`)
}

export async function getClass(id: string): Promise<Class> {
  const response = await api.get<Class>(`/courses/${id}`)
  return response.data
}

export async function getClassMembers(courseId: string): Promise<Member[]> {
  const response = await api.get<{ content: Member[] }>(`/courses/${courseId}/members`)
  return response.data.content
}
