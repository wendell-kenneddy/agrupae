import { useNavigate } from 'react-router-dom'
import type { Class } from '@/features/classes/types/classes.types'
import styles from './ClassCard.module.css'

interface ClassCardProps {
  class: Class
}

export function ClassCard({ class: c }: ClassCardProps) {
  const navigate = useNavigate()

  return (
    <div className={styles.card} onClick={() => navigate(`/classes/${c.id}`)}>
      <div className={styles.top}>
        <span className={styles.name}>{c.name}</span>
        <span className={`${styles.badge} ${c.role === 'OWNER' ? styles.owner : styles.student}`}>
          {c.role === 'OWNER' ? 'Responsável' : 'Estudante'}
        </span>
      </div>
      {c.description && <p className={styles.description}>{c.description}</p>}
      {/* bottom: memberCount, previewMembers, activeAssignments — aguardando backend */}
    </div>
  )
}
