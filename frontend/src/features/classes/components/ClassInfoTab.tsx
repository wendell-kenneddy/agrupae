import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useArchiveClass } from '@/features/classes/hooks/useArchiveClass.ts'
import { useGetClassMembers } from '@/features/classes/hooks/useGetClassMembers'
import type { Class } from '@/features/classes/types/classes.types'
import styles from './ClassInfoTab.module.css'

interface ClassInfoTabProps {
  course: Class
}

export function ClassInfoTab({ course }: ClassInfoTabProps) {
  const navigate = useNavigate()
  const { handleArchive, isLoading } = useArchiveClass(course.id)
  const { members, isLoading: isMembersLoading } = useGetClassMembers(course.id)
  const isOwner = course.role === 'OWNER'
  const [showArchiveModal, setShowArchiveModal] = useState(false)

  function handleCopyCode() {
    navigator.clipboard.writeText(course.inviteCode)
  }

  function handleConfirmArchive() {
    setShowArchiveModal(false)
    handleArchive()
  }

  return (
    <div className={styles.container}>
      <div className={styles.section}>
        <p className={styles.label}>Nome da turma</p>
        <p className={styles.value}>{course.name}</p>
      </div>

      <div className={styles.section}>
        <p className={styles.label}>Quantidade de membros</p>
        <p className={styles.value}>
          {isMembersLoading ? 'Carregando...' : `${members.length} ${members.length === 1 ? 'membro' : 'membros'}`}
        </p>
      </div>

      <div className={styles.inviteSection}>
        <p className={styles.inviteLabel}>Código de convite</p>
        <button className={styles.inviteCode} onClick={handleCopyCode}>
          <span>{course.inviteCode}</span>
          <svg
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
          >
            <rect x="9" y="9" width="13" height="13" rx="2" />
            <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" />
          </svg>
        </button>
      </div>

      {isOwner && (
        <div className={styles.actions}>
          <button
            className={styles.transferBtn}
            onClick={() => navigate(`/classes/${course.id}/transfer`)}
          >
            Transferir responsabilidade
          </button>
          <button
            className={styles.archiveBtn}
            onClick={() => setShowArchiveModal(true)}
            disabled={isLoading}
          >
            {isLoading ? 'Arquivando...' : 'Arquivar turma'}
          </button>
        </div>
      )}

      {showArchiveModal && (
        <>
          <div className={styles.overlay} onClick={() => setShowArchiveModal(false)} />
          <div className={styles.modal}>
            <button className={styles.closeBtn} onClick={() => setShowArchiveModal(false)}>
              <svg
                width="34"
                height="34"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="3.5"
                strokeLinecap="round"
              >
                <path d="M18 6L6 18M6 6l12 12" />
              </svg>
            </button>
            <p className={styles.modalTitle}>
              Arquivar a turma <strong>{course.name}</strong>?
            </p>
            <p className={styles.modalWarning}>A turma ficará inacessível para todos os membros.</p>
            <div className={styles.modalActions}>
              <button className={styles.cancelBtn} onClick={() => setShowArchiveModal(false)}>
                Cancelar
              </button>
              <button
                className={styles.confirmBtn}
                onClick={handleConfirmArchive}
                disabled={isLoading}
              >
                {isLoading ? 'Arquivando...' : 'Arquivar'}
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  )
}
