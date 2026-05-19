import type { Class } from '@/features/classes/types/classes.types'
import styles from './ClassAssignmentsTab.module.css'

interface ClassAssignmentsTabProps {
  course: Class
}

const assignmentsMock = [
  {
    id: '1',
    name: 'Modelagem Relacional - Sistema de Biblioteca',
    deadline: '15/05/2026',
    groupsFormed: 4,
    totalGroups: 8,
  },
  {
    id: '2',
    name: 'Implementação de Consultas SQL Avançadas',
    deadline: '28/05/2026',
    groupsFormed: 2,
    totalGroups: 8,
  },
]

export function ClassAssignmentsTab({ course }: ClassAssignmentsTabProps) {
  const isOwner = course.role === 'OWNER'

  return (
    <div className={styles.container}>
      {isOwner && <button className={styles.createBtn}>Criar trabalho</button>}
      <div className={styles.list}>
        {assignmentsMock.map((a) => (
          <div key={a.id} className={styles.card}>
            <div className={styles.cardTop}>
              <div>
                <p className={styles.cardName}>{a.name}</p>
                <p className={styles.cardDeadline}>Prazo: {a.deadline}</p>
              </div>
              <button className={styles.menuBtn}>⋮</button>
            </div>
            <hr className={styles.divider} />
            <div className={styles.cardBottom}>
              <div className={styles.groupsInfo}>
                <svg
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                >
                  <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                  <circle cx="9" cy="7" r="4" />
                  <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" />
                </svg>
                <span>Grupos formados</span>
                <span className={styles.groupsCount}>
                  {a.groupsFormed}/{a.totalGroups}
                </span>
              </div>
              <div className={styles.progressBar}>
                <div
                  className={styles.progressFill}
                  style={{ width: `${(a.groupsFormed / a.totalGroups) * 100}%` }}
                />
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
