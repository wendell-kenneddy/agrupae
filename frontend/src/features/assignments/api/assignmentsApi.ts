import api from '@/lib/axios'
import type {
  Assignment,
  AssignmentArtifact,
  AddArtifactRequest,
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

export async function getAssignments(courseId: string): Promise<Assignment[]> {
  const response = await api.get<{ content: Assignment[] }>(`/courses/${courseId}/assignments`)
  return response.data.content
}

export async function getAssignment(courseId: string, assignmentId: string): Promise<Assignment> {
  const response = await api.get<Assignment>(`/courses/${courseId}/assignments/${assignmentId}`)
  return response.data
}

export async function archiveAssignment(courseId: string, assignmentId: string): Promise<void> {
  await api.post(`/courses/${courseId}/assignments/${assignmentId}/archive`)
}

export async function getArtifacts(
  courseId: string,
  assignmentId: string
): Promise<AssignmentArtifact[]> {
  const response = await api.get<AssignmentArtifact[]>(
    `/courses/${courseId}/assignments/${assignmentId}/artifacts`
  )
  return response.data
}

export async function addArtifact(
  courseId: string,
  assignmentId: string,
  data: AddArtifactRequest
): Promise<AssignmentArtifact> {
  const response = await api.post<AssignmentArtifact>(
    `/courses/${courseId}/assignments/${assignmentId}/artifacts`,
    data
  )
  return response.data
}
