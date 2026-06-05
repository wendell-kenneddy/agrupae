import { useNavigate } from 'react-router-dom'
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
  const navigate = useNavigate()
  const isOwner = course.role === 'OWNER'

  return (
    <div className={styles.container}>
      {isOwner && (
        <button
          className={styles.createBtn}
          onClick={() => navigate(`/classes/${course.id}/assignments/create`)}
        >
          Criar trabalho
        </button>
      )}
      <div className={styles.list}>
        {assignmentsMock.map((a) => (
          <div
            key={a.id}
            className={styles.card}
            onClick={() => navigate(`/classes/${course.id}/assignments/${a.id}`)}
          >
            <div className={styles.cardTop}>
              <div className={styles.cardInfo}>
                <p className={styles.cardName}>{a.name}</p>
                <p className={styles.cardDeadline}>Prazo: {a.deadline}</p>
              </div>
              {isOwner && (
                <button
                  className={styles.editBtn}
                  onClick={(e) => {
                    e.stopPropagation()
                    navigate(`/classes/${course.id}/assignments/${a.id}/edit`)
                  }}
                >
                  <svg
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2.5"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  >
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                  </svg>
                </button>
              )}
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
