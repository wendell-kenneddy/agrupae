import { useMutation, useQueryClient } from '@tanstack/react-query'
import { archiveAssignment } from '@/features/assignments/api/assignmentsApi'

export function useArchiveAssignment(courseId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (assignmentId: string) => archiveAssignment(courseId, assignmentId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['assignments', courseId] })
    },
  })

  return { archive: mutateAsync, isLoading: isPending }
}
