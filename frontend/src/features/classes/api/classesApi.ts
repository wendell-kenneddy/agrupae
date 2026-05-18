import api from '@/lib/axios'
import type {
  Class,
  CreateClassRequest,
  JoinClassRequest,
} from '@/features/classes/types/classes.types'

export async function createClass(data: CreateClassRequest): Promise<Class> {
  const response = await api.post<Class>('/courses', data)
  return response.data
}

export async function joinClass(data: JoinClassRequest): Promise<Class> {
  const response = await api.post<Class>('/courses/join', data)
  return response.data
}
