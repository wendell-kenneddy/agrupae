import { useNavigate } from 'react-router-dom'
import type { Class } from '@/features/classes/types/classes.types'
import { useGetClassMembers } from '@/features/classes/hooks/useGetClassMembers'
import { useGetAssignments } from '@/features/assignments/hooks/useGetAssignments'
import styles from './ClassCard.module.css'

interface ClassCardProps {
  class: Class
}

export function ClassCard({ class: c }: ClassCardProps) {
  const navigate = useNavigate()

  // Fetch members and assignments to display on the card
  const { members } = useGetClassMembers(c.id)
  const { assignments } = useGetAssignments(c.id)

  const students = (members ?? [])
    .sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())

  const activeAssignmentsCount = (assignments ?? []).filter((a) => !a.isArchived).length

  const displayedStudents = students.slice(0, 3)
  const remainingCount = students.length - 3

  return (
    <div
      className={`${styles.card} ${c.archived ? styles.archivedCard : ''}`}
      onClick={() => navigate(`/classes/${c.id}`)}
    >
      <div className={styles.top}>
        <div className={styles.titleContainer}>
          <span className={styles.name}>{c.name}</span>
          {c.description && <p className={styles.description}>{c.description}</p>}
        </div>
        <span className={`${styles.badge} ${c.role === 'OWNER' ? styles.owner : styles.student}`}>
          {c.role === 'OWNER' ? 'Responsável' : 'Estudante'}
        </span>
      </div>

      <hr className={styles.divider} />

      <div className={styles.bottom}>
        {students.length > 0 && (
          <div className={styles.avatars}>
            {displayedStudents.map((student) => {
              const avatarUrl = `https://api.dicebear.com/7.x/avataaars/svg?seed=${encodeURIComponent(student.name)}`
              return (
                <div key={student.id} className={styles.avatar} style={{ padding: 0, overflow: 'hidden' }}>
                  <img
                    src={avatarUrl}
                    alt={student.name}
                    style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                  />
                </div>
              )
            })}
            {students.length >= 4 && (
              <div className={styles.avatarExtra}>
                +{remainingCount}
              </div>
            )}
          </div>
        )}

        <div className={styles.assignments}>
          <svg
            width="14"
            height="14"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2.5"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2" />
            <rect x="8" y="2" width="8" height="4" rx="1" ry="1" />
          </svg>
          <span>{activeAssignmentsCount} trabalhos ativos</span>
        </div>
      </div>
    </div>
  )
}

