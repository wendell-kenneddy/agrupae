import api from '@/lib/axios'
import type {
  Class,
  CreateClassRequest,
  JoinClassRequest,
} from '@/features/classes/types/classes.types'
import type { Page } from '@/types/page'

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

export async function transferOwnership(courseId: string, newLeaderId: string): Promise<Class> {
  const response = await api.post<Class>(`/courses/${courseId}/transfer`, { newLeaderId })
  return response.data
}
