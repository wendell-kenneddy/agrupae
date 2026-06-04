import { useMutation } from '@tanstack/react-query'
import { createAssignment } from '@/features/assignments/api/assignmentsApi'
import type { CreateAssignmentRequest } from '@/features/assignments/types/assignments.types'

export function useCreateAssignment(courseId: string) {
  const { mutateAsync, isPending } = useMutation({
    mutationFn: (data: CreateAssignmentRequest) => createAssignment(courseId, data),
  })

  return { create: mutateAsync, isLoading: isPending }
}
