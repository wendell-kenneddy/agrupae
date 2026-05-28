import { useQuery } from '@tanstack/react-query'
import { getCourses } from '@/features/classes/api/classesApi'

export function useGetCourses() {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['courses'],
    queryFn: getCourses,
  })

  return { courses: data ?? [], isLoading, isError }
}
