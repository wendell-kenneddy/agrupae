import { useMutation } from '@tanstack/react-query'
import { editAssignment } from '@/features/assignments/api/assignmentsApi'
import type { CreateAssignmentRequest } from '@/features/assignments/types/assignments.types'

export function useEditAssignment(courseId: string, assignmentId: string) {
  const { mutateAsync, isPending } = useMutation({
    mutationFn: (data: CreateAssignmentRequest) => editAssignment(courseId, assignmentId, data),
  })

  return { edit: mutateAsync, isLoading: isPending }
}
