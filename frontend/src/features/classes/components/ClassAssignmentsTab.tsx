import { useState } from 'react'
import type { MouseEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useGetAssignments } from '@/features/assignments/hooks/useGetAssignments'
import type { Class } from '@/features/classes/types/classes.types'
import type { Assignment } from '@/features/assignments/types/assignments.types'
import { useArchiveAssignment } from '@/features/assignments/hooks/useArchiveAssignment'

import styles from './ClassAssignmentsTab.module.css'

interface ClassAssignmentsTabProps {
  course: Class
}

export function ClassAssignmentsTab({ course }: ClassAssignmentsTabProps) {
  const navigate = useNavigate()
  const isOwner = course.role === 'OWNER'
  const { assignments, isLoading, isError } = useGetAssignments(course.id)
  const [openMenu, setOpenMenu] = useState<string | null>(null)
  const [menuPos, setMenuPos] = useState({ top: 0, right: 0 })
  const [archivingId, setArchivingId] = useState<string | null>(null)
  const { archive, isLoading: isArchiving } = useArchiveAssignment(course.id)

  function handleMenuOpen(e: MouseEvent<HTMLButtonElement>, id: string) {
    e.stopPropagation()
    const rect = (e.currentTarget as HTMLElement).getBoundingClientRect()
    setMenuPos({ top: rect.bottom + 4, right: window.innerWidth - rect.right })
    setOpenMenu(id)
  }

  function formatDate(iso: string) {
    return new Date(iso).toLocaleDateString('pt-BR')
  }

  return (
    <>
      <div className={styles.container}>
        {isOwner && (
          <button
            className={styles.createBtn}
            onClick={() => navigate(`/classes/${course.id}/assignments/create`)}
          >
            Criar trabalho
          </button>
        )}

        {isLoading && <p className={styles.feedback}>Carregando trabalhos...</p>}
        {isError && <p className={styles.feedback}>Erro ao carregar trabalhos.</p>}

        <div className={styles.list}>
          {assignments.map((a: Assignment) => (
            <div
              key={a.id}
              className={styles.card}
              onClick={() => navigate(`/classes/${course.id}/assignments/${a.id}`)}
            >
              <div className={styles.cardTop}>
                <div className={styles.cardInfo}>
                  <p className={styles.cardName}>{a.name}</p>
                  {a.dueDate && (
                    <p className={styles.cardDeadline}>Prazo: {formatDate(a.dueDate)}</p>
                  )}
                </div>
                {isOwner && (
                  <button className={styles.menuBtn} onClick={(e) => handleMenuOpen(e, a.id)}>
                    ⋮
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
                    0/{a.assignmentFlags.maxGroups === 999 ? '∞' : a.assignmentFlags.maxGroups}
                  </span>
                </div>
                <div className={styles.progressBar}>
                  <div className={styles.progressFill} style={{ width: '0%' }} />
                </div>
              </div>
            </div>
          ))}
        </div>

        {openMenu && (
          <>
            <div className={styles.overlay} onClick={() => setOpenMenu(null)} />
            <div className={styles.menu} style={{ top: menuPos.top, right: menuPos.right }}>
              <button
                className={styles.menuItem}
                onClick={() => {
                  setOpenMenu(null)
                  navigate(`/classes/${course.id}/assignments/${openMenu}/edit`)
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
                Editar
              </button>
              <button
                className={styles.menuItem}
                onClick={() => {
                  setOpenMenu(null)
                  setArchivingId(openMenu)
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
                  <polyline points="21 8 21 21 3 21 3 8" />
                  <rect x="1" y="3" width="22" height="5" />
                  <line x1="10" y1="12" x2="14" y2="12" />
                </svg>
                Arquivar
              </button>
            </div>
          </>
        )}
      </div>

      {archivingId && (
        <>
          <div className={styles.overlay} onClick={() => setArchivingId(null)} />
          <div className={styles.archiveModal}>
            <button className={styles.archiveCloseBtn} onClick={() => setArchivingId(null)}>
              <svg
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2.5"
                strokeLinecap="round"
              >
                <path d="M18 6L6 18M6 6l12 12" />
              </svg>
            </button>
            <p className={styles.archiveTitle}>
              Arquivar <strong>{assignments.find((a) => a.id === archivingId)?.name}</strong>?
            </p>
            <p className={styles.archiveWarning}>
              O trabalho ficará inacessível para todos os membros. Esta ação não pode ser desfeita.
            </p>
            <div className={styles.archiveActions}>
              <button className={styles.cancelBtn} onClick={() => setArchivingId(null)}>
                Cancelar
              </button>
              <button
                className={styles.confirmBtn}
                onClick={async () => {
                  if (archivingId) {
                    await archive(archivingId)
                    setArchivingId(null)
                  }
                }}
                disabled={isArchiving}
              >
                {isArchiving ? 'Arquivando...' : 'Arquivar'}
              </button>
            </div>
          </div>
        </>
      )}
    </>
  )
}
