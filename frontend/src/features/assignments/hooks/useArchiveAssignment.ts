import { useMutation, useQueryClient } from '@tanstack/react-query'
import { archiveAssignment } from '@/features/assignments/api/assignmentsApi'
import { toast } from '@/components/ui/useToast'

export function useArchiveAssignment(courseId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (assignmentId: string) => archiveAssignment(courseId, assignmentId),
    onSuccess: () => {
      toast.success('Trabalho arquivado com sucesso!')
      queryClient.invalidateQueries({ queryKey: ['assignments', courseId] })
    },
  })

  return { archive: mutateAsync, isLoading: isPending }
}
