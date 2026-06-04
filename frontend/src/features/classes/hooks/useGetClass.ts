import { useQuery } from '@tanstack/react-query'
import { getClass } from '@/features/classes/api/classesApi'

export function useGetClass(id: string) {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['course', id],
    queryFn: () => getClass(id),
  })

  return { course: data, isLoading, isError }
}
