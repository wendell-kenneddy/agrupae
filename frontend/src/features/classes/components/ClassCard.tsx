import { useNavigate } from 'react-router-dom'
import type { Class } from '@/features/classes/types/classes.types'
import styles from './ClassCard.module.css'

// import { FaRegClipboard } from 'react-icons/fa'

interface ClassCardProps {
  class: Class
}

// function AvatarPlaceholder({ name }: { name: string }) {
//   const initials = name
//     .split(' ')
//     .slice(0, 2)
//     .map((n) => n[0])
//     .join('')

//   return <div className={styles.avatar}>{initials}</div>
// }

export function ClassCard({ class: c }: ClassCardProps) {
  const navigate = useNavigate()
  // const extraMembers = c.memberCount - c.previewMembers.length

  return (
    <div className={styles.card} onClick={() => navigate(`/classes/${c.id}`)}>
      <div className={styles.top}>
        <span className={styles.name}>{c.name}</span>
        {/* <span className={`${styles.badge} ${c.role === 'OWNER' ? styles.owner : styles.student}`}>
          {c.role === 'OWNER' ? 'Responsável' : 'Estudante'}
        </span> */}
      </div>
      <hr className={styles.divider} />
      {/* <div className={styles.bottom}> */}
      {/* <div className={styles.avatars}>
          {c.previewMembers.map((m) => (
            <AvatarPlaceholder key={m.id} name={m.name} />
          ))}
          {/* <div className={styles.avatarExtra}>+{extraMembers}</div> */}
      {/* </div> */}
      {/* <div className={styles.assignments}>
          <FaRegClipboard />
          <span>{c.activeAssignments} trabalhos ativos</span>
        </div> */}
      {/* </div> */}
    </div>
  )
}
