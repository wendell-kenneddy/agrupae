import api from '@/lib/axios'
import type { Group, CreateGroupRequest } from '@/features/group/types/groups.types'

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
