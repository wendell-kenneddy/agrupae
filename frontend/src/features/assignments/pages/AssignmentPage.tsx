import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useGetArtifacts } from '@/features/assignments/hooks/useGetArtifacts'
import { useAddArtifact } from '@/features/assignments/hooks/useAddArtifact'
import { useGetAssignment } from '@/features/assignments/hooks/useGetAssignment'
import { useGetClass } from '@/features/classes/hooks/useGetClass'
import { useCreateGroup } from '@/features/group/hooks/useCreateGroup'

import { AvatarMenu } from '@/components/ui/AvatarMenu'
import type { AssignmentArtifact } from '@/features/assignments/types/assignments.types'

import styles from './AssignmentPage.module.css'

export function AssignmentPage() {
  const navigate = useNavigate()
  const { id: courseId, assignmentId } = useParams<{ id: string; assignmentId: string }>()

  const { assignment, isLoading, isError } = useGetAssignment(courseId!, assignmentId!)
  const { course } = useGetClass(courseId!)
  const { create: createGroup, isLoading: isCreatingGroup } = useCreateGroup(courseId!, assignmentId!)

  const { artifacts, isLoading: isLoadingArtifacts } = useGetArtifacts(courseId!, assignmentId!)
  const { add, isLoading: isAdding } = useAddArtifact(courseId!, assignmentId!)
  const [modalArtifact, setModalArtifact] = useState<AssignmentArtifact | null | 'new'>(null)
  const [formName, setFormName] = useState('')
  const [formDescription, setFormDescription] = useState('')
  const [formLink, setFormLink] = useState('')

  const [showCreateGroup, setShowCreateGroup] = useState(false)
  const [groupName, setGroupName] = useState('')
  const [groupOpen, setGroupOpen] = useState(true)

  const isOwner = course?.role === 'OWNER'
  const canCreateGroup =
    assignment?.assignmentFlags.studentsCanCreateGroups === true &&
    !assignment?.archived

  if (isLoading) return <div className={styles.feedback}>Carregando...</div>
  if (isError || !assignment) return <div className={styles.feedback}>Trabalho não encontrado.</div>

  const dueDate = assignment.dueDate
    ? new Date(assignment.dueDate).toLocaleDateString('pt-BR')
    : null

  const maxGroups = assignment.assignmentFlags.maxGroups

  function openNewArtifact() {
    setFormName('')
    setFormDescription('')
    setFormLink('')
    setModalArtifact('new')
  }

  function openEditArtifact(artifact: AssignmentArtifact) {
    setFormName(artifact.name)
    setFormDescription(artifact.description ?? '')
    setFormLink(artifact.resourceLink)
    setModalArtifact(artifact)
  }

  async function handleSave() {
    if (!formName.trim() || !formLink.trim()) return
    if (modalArtifact === 'new') {
      await add({
        name: formName.trim(),
        description: formDescription.trim(),
        resourceLink: formLink.trim(),
      })
    }
    setModalArtifact(null)
  }

  function openCreateGroup() {
    setGroupName('')
    setGroupOpen(true)
    setShowCreateGroup(true)
  }

  async function handleCreateGroup() {
    if (!groupName.trim()) return
    try {
      await createGroup({ name: groupName.trim(), open: groupOpen })
      setShowCreateGroup(false)
    } catch {
      // error handled by the hook's onError
    }
  }

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <button className={styles.backBtn} onClick={() => navigate(-1)}>
          <svg
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="3"
          >
            <path d="M19 12H5M12 5l-7 7 7 7" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
        </button>
        <span className={styles.headerTitle}>{assignment.name}</span>
        <AvatarMenu />
      </header>

      <div className={styles.content}>
        <div className={styles.infoCard}>
          <div className={styles.infoRow}>
            {dueDate && (
              <span className={styles.infoItem}>
                <strong>Prazo:</strong> {dueDate}
              </span>
            )}
            <span className={styles.infoItem}>
              <strong>Limite de membros:</strong>{' '}
              {assignment.assignmentFlags.maxGroupMembers === 999
                ? 'Sem limite'
                : assignment.assignmentFlags.maxGroupMembers}
            </span>
          </div>

          <hr className={styles.divider} />

          {assignment.description && <p className={styles.description}>{assignment.description}</p>}

          {isLoadingArtifacts && <p className={styles.feedbackSmall}>Carregando artefatos...</p>}

          {!isLoadingArtifacts && artifacts.length > 0 && (
            <div className={styles.artifacts}>
              {artifacts.map((a) => (
                <div key={a.id} className={styles.artifactRow}>
                  <a
                    href={a.resourceLink}
                    target="_blank"
                    rel="noopener noreferrer"
                    className={styles.artifactLink}
                  >
                    <svg
                      width="14"
                      height="14"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2.5"
                      strokeLinecap="round"
                    >
                      <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71" />
                      <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71" />
                    </svg>
                    <span>
                      {a.name}
                      {a.description ? ` · ${a.description}` : ''}
                    </span>
                  </a>
                  {isOwner && (
                    <button className={styles.artifactEditBtn} onClick={() => openEditArtifact(a)}>
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
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                      </svg>
                    </button>
                  )}
                </div>
              ))}
            </div>
          )}

          {isOwner && (
            <button className={styles.editBtn} onClick={openNewArtifact}>
              Editar artefatos
            </button>
          )}
        </div>

        <div className={styles.groupsSection}>
          <div className={styles.groupsHeader}>
            <div className={styles.groupsTitle}>
              <svg
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
              >
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                <circle cx="9" cy="7" r="4" />
                <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" />
              </svg>
              <span>Grupos</span>
            </div>
            <span className={styles.groupsCount}>
              Limite: {maxGroups === 999 ? '∞' : maxGroups}
            </span>
          </div>

          <div className={styles.groupsList}>
            <p className={styles.emptyGroups}>Nenhum grupo formado ainda</p>
          </div>

          {canCreateGroup && (
            <button className={styles.createGroupBtn} onClick={openCreateGroup}>
              Criar grupo
            </button>
          )}
        </div>
      </div>

      {modalArtifact !== null && (
        <>
          <div className={styles.overlay} onClick={() => setModalArtifact(null)} />
          <div className={styles.modal}>
            <button className={styles.closeBtn} onClick={() => setModalArtifact(null)}>
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

            <p className={styles.modalTitle}>
              {modalArtifact === 'new' ? 'Adicionar artefato' : 'Editar artefato'}
            </p>

            <div className={styles.modalFields}>
              <input
                className={styles.modalInput}
                type="text"
                placeholder="Nome"
                value={formName}
                onChange={(e) => setFormName(e.target.value)}
              />
              <input
                className={styles.modalInput}
                type="text"
                placeholder="Descrição (opcional)"
                value={formDescription}
                onChange={(e) => setFormDescription(e.target.value)}
              />
              <input
                className={styles.modalInput}
                type="url"
                placeholder="Link"
                value={formLink}
                onChange={(e) => setFormLink(e.target.value)}
              />
            </div>

            <div className={styles.modalActions}>
              <button className={styles.cancelBtn} onClick={() => setModalArtifact(null)}>
                Cancelar
              </button>
              <button
                className={styles.confirmBtn}
                onClick={handleSave}
                disabled={!formName.trim() || !formLink.trim() || isAdding}
              >
                {isAdding ? 'Salvando...' : modalArtifact === 'new' ? 'Adicionar' : 'Salvar'}
              </button>
            </div>
          </div>
        </>
      )}

      {showCreateGroup && (
        <>
          <div className={styles.overlay} onClick={() => setShowCreateGroup(false)} />
          <div className={styles.modal}>
            <button className={styles.closeBtn} onClick={() => setShowCreateGroup(false)}>
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

            <p className={styles.modalTitle}>Criar grupo</p>

            <div className={styles.modalFields}>
              <input
                className={styles.modalInput}
                type="text"
                placeholder="Nome do grupo"
                value={groupName}
                onChange={(e) => setGroupName(e.target.value)}
              />

              <div className={styles.modeToggle}>
                <span className={styles.modeLabel}>Modo do grupo</span>
                <div className={styles.modeOptions}>
                  <button
                    className={`${styles.modeOption} ${groupOpen ? styles.modeOptionActive : ''}`}
                    onClick={() => setGroupOpen(true)}
                    type="button"
                  >
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M18 8h1a4 4 0 0 1 0 8h-1M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8zM6 1v3M10 1v3M14 1v3" />
                    </svg>
                    Aberto
                  </button>
                  <button
                    className={`${styles.modeOption} ${!groupOpen ? styles.modeOptionActive : ''}`}
                    onClick={() => setGroupOpen(false)}
                    type="button"
                  >
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                      <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                      <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                    </svg>
                    Fechado
                  </button>
                </div>
                <span className={styles.modeDescription}>
                  {groupOpen
                    ? 'Qualquer estudante da turma pode entrar diretamente.'
                    : 'Estudantes precisam solicitar entrada. Você aprova ou rejeita.'}
                </span>
              </div>
            </div>

            <div className={styles.modalActions}>
              <button className={styles.cancelBtn} onClick={() => setShowCreateGroup(false)}>
                Cancelar
              </button>
              <button
                className={styles.confirmBtn}
                onClick={handleCreateGroup}
                disabled={!groupName.trim() || isCreatingGroup}
              >
                {isCreatingGroup ? 'Criando...' : 'Criar'}
              </button>
            </div>
          </div>
        </>
      )}
    </main>
  )
}
