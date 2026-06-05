import api from '@/lib/axios'
import type {
  Assignment,
  CreateAssignmentRequest,
} from '@/features/assignments/types/assignments.types'

export async function createAssignment(
  courseId: string,
  data: CreateAssignmentRequest
): Promise<Assignment> {
  const response = await api.post<Assignment>(`/courses/${courseId}/assignments`, data)
  return response.data
}

export async function editAssignment(
  courseId: string,
  assignmentId: string,
  data: CreateAssignmentRequest
): Promise<Assignment> {
  const response = await api.put<Assignment>(
    `/courses/${courseId}/assignments/${assignmentId}`,
    data
  )
  return response.data
}
