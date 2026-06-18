import { useGetClassMembers } from '@/features/classes/hooks/useGetClassMembers'
import type { Class } from '@/features/classes/types/classes.types'
import { useAuth } from '@/app/providers/AuthContext'
import styles from './ClassMembersTab.module.css'

interface ClassMembersTabProps {
  course: Class
}

function MemberAvatar({ name }: { name: string }) {
  const avatarUrl = `https://api.dicebear.com/7.x/avataaars/svg?seed=${encodeURIComponent(name)}`

  return (
    <div className={styles.avatar} style={{ padding: 0, overflow: 'hidden' }}>
      <img src={avatarUrl} alt={name} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
    </div>
  )
}

export function ClassMembersTab({ course }: ClassMembersTabProps) {
  const { user } = useAuth()
  const { members, isLoading, isError } = useGetClassMembers(course.id)

  if (isLoading) return <div className={styles.feedback}>Carregando membros...</div>
  if (isError) return <div className={styles.feedback}>Erro ao carregar membros.</div>

  return (
    <div className={styles.container}>
      {members.map((m) => (
        <div key={m.id} className={styles.memberItem}>
          <MemberAvatar name={m.name} />
          <div className={styles.memberInfo}>
            <p className={styles.memberName}>
              {m.id === user?.id ? 'Você' : m.name}
              {m.id === course.leaderId && <span className={styles.responsibleTag}>Responsável</span>}
            </p>
            <p className={styles.memberEmail}>{m.email}</p>
          </div>
        </div>
      ))}
    </div>
  )
}
