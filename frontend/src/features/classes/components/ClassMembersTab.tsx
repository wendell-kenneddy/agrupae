import { useGetClassMembers } from '@/features/classes/hooks/useGetClassMembers'
import type { Class } from '@/features/classes/types/classes.types'
import styles from './ClassMembersTab.module.css'

interface ClassMembersTabProps {
  course: Class
}

function MemberAvatar({ name }: { name: string }) {
  const initials = name
    .split(' ')
    .slice(0, 2)
    .map((n) => n[0])
    .join('')

  return <div className={styles.avatar}>{initials}</div>
}

export function ClassMembersTab({ course }: ClassMembersTabProps) {
  const { members, isLoading, isError } = useGetClassMembers(course.id)

  if (isLoading) return <div className={styles.feedback}>Carregando membros...</div>
  if (isError) return <div className={styles.feedback}>Erro ao carregar membros.</div>

  return (
    <div className={styles.container}>
      {members.map((m) => (
        <div key={m.id} className={styles.memberItem}>
          <MemberAvatar name={m.name} />
          <div className={styles.memberInfo}>
            <p className={styles.memberName}>{m.name}</p>
            <p className={styles.memberEmail}>{m.email}</p>
          </div>
        </div>
      ))}
    </div>
  )
}
