import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useAuth } from '@/app/providers/AuthContext'
import { useGetAssignment } from '@/features/assignments/hooks/useGetAssignment'
import { useGetClass } from '@/features/classes/hooks/useGetClass'
import { useGetGroups } from '@/features/group/hooks/useGetGroups'
import { useGetGroupMembers } from '@/features/group/hooks/useGetGroupMembers'
import { useGetGroupArtifacts } from '@/features/group/hooks/useGetGroupArtifacts'
import { useAddGroupArtifact } from '@/features/group/hooks/useAddGroupArtifact'
import { useDeleteGroupArtifact } from '@/features/group/hooks/useDeleteGroupArtifact'
import { useToggleGroupArtifactDeliverable } from '@/features/group/hooks/useToggleGroupArtifactDeliverable'
import { useLeaveGroup } from '@/features/group/hooks/useLeaveGroup'
import { useRemoveGroupMember } from '@/features/group/hooks/useRemoveGroupMember'
import { useChangeGroupMode } from '@/features/group/hooks/useChangeGroupMode'
import { useDissolveGroup } from '@/features/group/hooks/useDissolveGroup'
import { useJoinOpenGroup } from '@/features/group/hooks/useJoinOpenGroup'
import { useRequestGroupEntry } from '@/features/group/hooks/useRequestGroupEntry'
import { useCancelGroupEntryRequest } from '@/features/group/hooks/useCancelGroupEntryRequest'
import { useGetMyEntryRequests } from '@/features/group/hooks/useGetMyEntryRequests'
import { AvatarMenu } from '@/components/ui/AvatarMenu'

import styles from './GroupPage.module.css'

export function GroupPage() {
  const navigate = useNavigate()
  const { id: courseId, assignmentId, groupId } = useParams<{ id: string; assignmentId: string; groupId: string }>()

  const { user } = useAuth()
  const { assignment, isLoading: isLoadingAssignment } = useGetAssignment(courseId!, assignmentId!)
  const { course } = useGetClass(courseId!)
  const { groupsData, isLoading: isLoadingGroups } = useGetGroups(courseId!, assignmentId!)
  const { members, isLoading: isLoadingMembers } = useGetGroupMembers(courseId!, assignmentId!, groupId!)

  // Find group in assignment groups list
  const group = groupsData?.groups.content.find((g) => g.id === groupId)
  const isMyGroup = groupsData?.myGroup?.id === groupId
  const hasGroup = !!groupsData?.myGroup

  const isMember = isMyGroup || members.some((m) => m.id === user?.id)

  const { artifacts, isLoading: isLoadingArtifacts } = useGetGroupArtifacts(courseId!, assignmentId!, groupId!, isMember)
  const { addArtifact, isLoading: isAddingArtifact } = useAddGroupArtifact(courseId!, assignmentId!, groupId!)
  const { deleteArtifact, isLoading: isDeletingArtifact } = useDeleteGroupArtifact(courseId!, assignmentId!, groupId!)
  const { toggleDeliverable, isLoading: isTogglingDeliverable } = useToggleGroupArtifactDeliverable(courseId!, assignmentId!, groupId!)

  const { leave: leaveGroup, isLoading: isLeaving } = useLeaveGroup(courseId!, assignmentId!)
  const { removeMember, isLoading: isRemovingMember } = useRemoveGroupMember(courseId!, assignmentId!)
  const { changeMode, isLoading: isChangingMode } = useChangeGroupMode(courseId!, assignmentId!)
  const { dissolve: dissolveGroup, isLoading: isDissolving } = useDissolveGroup(courseId!, assignmentId!)

  const { join, isLoading: isJoining } = useJoinOpenGroup(courseId!, assignmentId!)
  const { requestEntry, isLoading: isRequesting } = useRequestGroupEntry(courseId!, assignmentId!)
  const { cancel: cancelRequest, isLoading: isCancelling } = useCancelGroupEntryRequest(courseId!, assignmentId!)
  const { myRequests } = useGetMyEntryRequests(courseId!, assignmentId!)

  const [showAddLink, setShowAddLink] = useState(false)
  const [linkName, setLinkName] = useState('')
  const [linkUrl, setLinkUrl] = useState('')
  const [linkDescription, setLinkDescription] = useState('')

  const [showMenu, setShowMenu] = useState(false)

  // Confirm actions states
  const [showConfirmLeave, setShowConfirmLeave] = useState(false)
  const [showConfirmDissolve, setShowConfirmDissolve] = useState(false)
  const [removeMemberInfo, setRemoveMemberInfo] = useState<{ id: string; name: string } | null>(null)

  const isOwner = course?.role === 'OWNER'
  const isAssignmentActive = assignment ? !assignment.isArchived : false
  const isGroupLeader = group ? group.leaderId === user?.id : false

  useEffect(() => {
    if (!isLoadingAssignment && !isLoadingGroups && !isLoadingMembers) {
      if (!isMember && !isOwner) {
        navigate(`/classes/${courseId}/assignments/${assignmentId}`)
      }
    }
  }, [isLoadingAssignment, isLoadingGroups, isLoadingMembers, isMember, isOwner, navigate, courseId, assignmentId])

  if (isLoadingAssignment || isLoadingGroups || isLoadingMembers) {
    return <div className={styles.page}>Carregando...</div>
  }

  if (!group || !assignment || (!isMember && !isOwner)) {
    return null
  }

  const myRequest = myRequests.find((r) => r.groupId === groupId && (r.status === 'PENDING' || r.status === 'REJECTED'))
  const hasPendingRequest = myRequests.some((r) => r.status === 'PENDING')

  const maxMembers = assignment.assignmentFlags.maxGroupMembers
  const isFull = group.memberCount >= maxMembers

  const showLeaveOption = isMember && assignment.assignmentFlags.studentsCanLeaveGroups && isAssignmentActive
  const showToggleModeOption = isGroupLeader && assignment.assignmentFlags.groupLeaderCanChangeMode && isAssignmentActive
  const showDissolveOption =
    ((isGroupLeader && assignment.assignmentFlags.groupLeaderCanDissolve) ||
      (isOwner && assignment.assignmentFlags.supervisorCanEditGroups)) &&
    isAssignmentActive

  const hasMenuOptions = showLeaveOption || showToggleModeOption || showDissolveOption

  async function handleAddLink() {
    if (!linkName.trim() || !linkUrl.trim()) return
    try {
      await addArtifact({
        name: linkName.trim(),
        resourceLink: linkUrl.trim(),
        description: linkDescription.trim(),
        privateArtifact: true,
      })
      setShowAddLink(false)
      setLinkName('')
      setLinkUrl('')
      setLinkDescription('')
    } catch {}
  }

  async function handleDeleteLink(artifactId: string) {
    try {
      await deleteArtifact(artifactId)
    } catch {}
  }

  async function handleLeave() {
    try {
      await leaveGroup(groupId)
      setShowConfirmLeave(false)
      navigate(`/classes/${courseId}/assignments/${assignmentId}`)
    } catch {}
  }

  async function handleDissolve() {
    try {
      await dissolveGroup(groupId)
      setShowConfirmDissolve(false)
      navigate(`/classes/${courseId}/assignments/${assignmentId}`)
    } catch {}
  }

  async function handleToggleMode() {
    try {
      await changeMode({ groupId, open: !group.open })
      setShowMenu(false)
    } catch {}
  }

  async function handleRemove(memberId: string) {
    try {
      await removeMember({ groupId, memberId })
      setRemoveMemberInfo(null)
    } catch {}
  }

  async function handleJoinGroup() {
    try {
      await join(groupId)
    } catch {}
  }

  async function handleRequestGroupEntry() {
    try {
      await requestEntry(groupId)
    } catch {}
  }

  async function handleCancelRequestEntry() {
    if (myRequest && myRequest.status === 'PENDING') {
      try {
        await cancelRequest({ groupId, requestId: myRequest.id })
      } catch {}
    }
  }

  // Format resourceLink for display in mockup style
  function displayLink(url: string) {
    try {
      const cleanUrl = url.replace(/^(https?:\/\/)?(www\.)?/, '')
      return cleanUrl
    } catch {
      return url
    }
  }

  return (
    <main className={styles.page}>
      <header className={styles.header}>
          <button className={styles.backBtn} onClick={() => navigate(-1)}>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
              <path d="M19 12H5M12 5l-7 7 7 7" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </button>
          <span className={styles.headerTitle}>{group.name}</span>
          <AvatarMenu />
        </header>

        <div className={styles.content}>
          {/* Informações Card */}
          <div className={styles.infoCard}>
            <div className={styles.infoCardHeader}>
              <div className={styles.statusText}>
                {group.open ? (
                  <>
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                      <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                      <path d="M7 11V7a5 5 0 0 1 9.9-1" />
                    </svg>
                    <span>
                      Aberto - {group.memberCount}/{maxMembers === 999 ? 'Sem limite' : `${maxMembers} membros`}
                    </span>
                  </>
                ) : (
                  <>
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                      <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                      <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                    </svg>
                    <span>
                      Fechado - {group.memberCount}/{maxMembers === 999 ? 'Sem limite' : `${maxMembers} membros`}
                    </span>
                  </>
                )}
              </div>

              {hasMenuOptions && (
                <button className={styles.menuButton} onClick={() => setShowMenu(true)}>
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                    <circle cx="12" cy="5" r="1.5" />
                    <circle cx="12" cy="12" r="1.5" />
                    <circle cx="12" cy="19" r="1.5" />
                  </svg>
                </button>
              )}
            </div>

            {showMenu && (
              <>
                <div className={styles.dropdownOverlay} onClick={() => setShowMenu(false)} />
                <div className={styles.dropdownMenu}>
                  {showToggleModeOption && (
                    <button className={styles.dropdownItem} onClick={handleToggleMode} disabled={isChangingMode}>
                      {group.open ? 'Alterar para Fechado' : 'Alterar para Aberto'}
                    </button>
                  )}
                  {showLeaveOption && (
                    <button
                      className={styles.dropdownItem}
                      onClick={() => {
                        setShowMenu(false)
                        setShowConfirmLeave(true)
                      }}
                    >
                      Sair do grupo
                    </button>
                  )}
                  {showDissolveOption && (
                    <button
                      className={`${styles.dropdownItem} ${styles.dropdownItemDelete}`}
                      onClick={() => {
                        setShowMenu(false)
                        setShowConfirmDissolve(true)
                      }}
                      disabled={isDissolving}
                    >
                      Dissolver grupo
                    </button>
                  )}
                </div>
              </>
            )}

            <hr className={styles.cardDivider} />

            <div className={styles.infoDetails}>
              <p>
                <strong>Trabalho:</strong> {assignment.name}
              </p>
              {assignment.description && <p>{assignment.description}</p>}
            </div>
          </div>

          {/* Action buttons for non-members */}
          {!isMember && !hasGroup && isAssignmentActive && (
            <div style={{ marginTop: -10 }}>
              {group.open && !isFull && (
                <button className={styles.addLinkBtn} onClick={handleJoinGroup} disabled={isJoining}>
                  {isJoining ? 'Entrando...' : 'Entrar no grupo'}
                </button>
              )}
              {!group.open && !isFull && !myRequest && !hasPendingRequest && (
                <button className={styles.addLinkBtn} onClick={handleRequestGroupEntry} disabled={isRequesting}>
                  {isRequesting ? 'Solicitando...' : 'Solicitar entrada'}
                </button>
              )}
              {!group.open && myRequest?.status === 'PENDING' && (
                <button
                  className={styles.addLinkBtn}
                  style={{ background: '#d97706' }}
                  onClick={handleCancelRequestEntry}
                  disabled={isCancelling}
                >
                  {isCancelling ? 'Cancelando...' : 'Cancelar solicitação'}
                </button>
              )}
              {myRequest?.status === 'REJECTED' && (
                <div style={{ textAlign: 'center', fontSize: '0.85rem', color: '#dc2626', fontWeight: 600, padding: 8 }}>
                  Sua solicitação de entrada foi rejeitada.
                </div>
              )}
            </div>
          )}

          {/* Links do Grupo Card */}
          <div className={styles.linksCard}>
            <span className={styles.linksTitle}>Links do grupo</span>

            <div className={styles.linksList}>
              {isLoadingArtifacts && <div style={{ fontSize: '0.8rem', color: '#1e3a8a' }}>Carregando links...</div>}
              {!isLoadingArtifacts && artifacts.length === 0 && (
                <div style={{ fontSize: '0.8rem', color: '#1e3a8a', opacity: 0.7 }}>Nenhum link adicionado ainda.</div>
              )}
              {!isLoadingArtifacts &&
                artifacts.map((a) => (
                  <div key={a.id} className={styles.linkRow}>
                    <a
                      href={a.resourceLink}
                      target="_blank"
                      rel="noopener noreferrer"
                      className={styles.linkText}
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
                        {a.deliverable && (
                          <span className={styles.deliverableBadge}>Entregável</span>
                        )}
                      </span>
                    </a>
                    {isMember && isAssignmentActive && (
                      <div className={styles.linkActions}>
                        <button
                          className={`${styles.linkExportBtn} ${a.deliverable ? styles.linkExportBtnActive : ''}`}
                          onClick={() => toggleDeliverable({ artifactId: a.id, deliverable: !a.deliverable })}
                          disabled={isTogglingDeliverable}
                          title={a.deliverable ? "Desmarcar como entregável" : "Marcar como entregável"}
                        >
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
                            <path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8M16 6l-4-4-4 4M12 2v13"/>
                          </svg>
                        </button>
                        <button
                          className={styles.linkDeleteBtn}
                          onClick={() => handleDeleteLink(a.id)}
                          disabled={isDeletingArtifact}
                          title="Excluir link"
                        >
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
                            <path d="M3 6h18M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2M10 11v6M14 11v6" />
                          </svg>
                        </button>
                      </div>
                    )}
                  </div>
                ))}
            </div>

            {isMember && isAssignmentActive && (
              <button className={styles.addLinkBtn} onClick={() => setShowAddLink(true)}>
                Adicionar artefato
              </button>
            )}
          </div>

          {/* Membros Section */}
          <div className={styles.membersSection}>
            <h2 className={styles.sectionTitle}>Membros</h2>

            <div className={styles.memberList}>
              {members.map((member) => (
                <div key={member.id} className={styles.memberRow}>
                  <div className={styles.memberLeft}>
                    <img
                      src={`https://api.dicebear.com/7.x/avataaars/svg?seed=${encodeURIComponent(member.name)}`}
                      alt={member.name}
                      className={styles.memberAvatar}
                    />
                    <div className={styles.memberInfo}>
                      <span className={styles.memberName}>
                        {member.name}
                        {member.isLeader && <span className={styles.leaderBadge}>Líder</span>}
                      </span>
                      <span className={styles.memberEmail}>{member.email}</span>
                    </div>
                  </div>

                  {/* Remove member button for Group Leader */}
                  {isGroupLeader &&
                    !member.isLeader &&
                    assignment.assignmentFlags.groupLeaderCanRemoveMembers &&
                    isAssignmentActive && (
                      <button
                        className={styles.memberRemoveBtn}
                        onClick={() => setRemoveMemberInfo({ id: member.id, name: member.name })}
                        title="Remover membro do grupo"
                      >
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                          <path d="M3 6h18M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2M10 11v6M14 11v6" />
                        </svg>
                      </button>
                    )}
                </div>
              ))}
            </div>
          </div>
        </div>

      {/* Add Link Modal */}
      {showAddLink && (
        <>
          <div className={styles.overlay} onClick={() => setShowAddLink(false)} />
          <div className={styles.modal}>
            <button className={styles.closeBtn} onClick={() => setShowAddLink(false)}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M18 6L6 18M6 6l12 12" />
              </svg>
            </button>

            <p className={styles.modalTitle}>Adicionar artefato</p>

            <div className={styles.modalFields}>
              <input
                className={styles.modalInput}
                type="text"
                placeholder="Nome"
                value={linkName}
                onChange={(e) => setLinkName(e.target.value)}
              />
              <input
                className={styles.modalInput}
                type="text"
                placeholder="Descrição (opcional)"
                value={linkDescription}
                onChange={(e) => setLinkDescription(e.target.value)}
              />
              <input
                className={styles.modalInput}
                type="url"
                placeholder="Link"
                value={linkUrl}
                onChange={(e) => setLinkUrl(e.target.value)}
              />
            </div>

            <div className={styles.modalActions}>
              <button className={styles.cancelBtn} onClick={() => setShowAddLink(false)}>
                Cancelar
              </button>
              <button
                className={styles.confirmBtn}
                onClick={handleAddLink}
                disabled={!linkName.trim() || !linkUrl.trim() || isAddingArtifact}
              >
                {isAddingArtifact ? 'Adicionando...' : 'Adicionar'}
              </button>
            </div>
          </div>
        </>
      )}

      {/* Confirm Leave Modal */}
      {showConfirmLeave && (
        <>
          <div className={styles.overlay} onClick={() => setShowConfirmLeave(false)} />
          <div className={styles.confirmModal}>
            <button className={styles.confirmModalCloseBtn} onClick={() => setShowConfirmLeave(false)}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M18 6L6 18M6 6l12 12" />
              </svg>
            </button>

            <p className={styles.confirmModalTitle}>Sair do grupo</p>
            <p className={styles.confirmModalWarning}>
              Tem certeza que deseja sair deste grupo? Se você for o líder, a liderança será transferida para o membro mais
              antigo.
            </p>

            <div className={styles.confirmModalActions}>
              <button className={styles.confirmModalCancelBtn} onClick={() => setShowConfirmLeave(false)}>
                Cancelar
              </button>
              <button className={styles.confirmModalConfirmBtn} onClick={handleLeave} disabled={isLeaving}>
                {isLeaving ? 'Saindo...' : 'Sair'}
              </button>
            </div>
          </div>
        </>
      )}

      {/* Confirm Dissolve Modal */}
      {showConfirmDissolve && (
        <>
          <div className={styles.overlay} onClick={() => setShowConfirmDissolve(false)} />
          <div className={styles.confirmModal}>
            <button className={styles.confirmModalCloseBtn} onClick={() => setShowConfirmDissolve(false)}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M18 6L6 18M6 6l12 12" />
              </svg>
            </button>

            <p className={styles.confirmModalTitle}>Dissolver grupo</p>
            <p className={styles.confirmModalWarning}>
              Tem certeza que deseja dissolver este grupo? Todos os membros serão removidos e o grupo será excluído.
            </p>

            <div className={styles.confirmModalActions}>
              <button className={styles.confirmModalCancelBtn} onClick={() => setShowConfirmDissolve(false)}>
                Cancelar
              </button>
              <button className={styles.confirmModalConfirmBtn} onClick={handleDissolve} disabled={isDissolving}>
                {isDissolving ? 'Dissolvendo...' : 'Dissolver'}
              </button>
            </div>
          </div>
        </>
      )}

      {/* Confirm Remove Member Modal */}
      {removeMemberInfo !== null && (
        <>
          <div className={styles.overlay} onClick={() => setRemoveMemberInfo(null)} />
          <div className={styles.confirmModal}>
            <button className={styles.confirmModalCloseBtn} onClick={() => setRemoveMemberInfo(null)}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M18 6L6 18M6 6l12 12" />
              </svg>
            </button>

            <p className={styles.confirmModalTitle}>Remover membro</p>
            <p className={styles.confirmModalWarning}>
              Tem certeza que deseja remover {removeMemberInfo.name} deste grupo?
            </p>

            <div className={styles.confirmModalActions}>
              <button className={styles.confirmModalCancelBtn} onClick={() => setRemoveMemberInfo(null)}>
                Cancelar
              </button>
              <button
                className={styles.confirmModalConfirmBtn}
                onClick={() => handleRemove(removeMemberInfo.id)}
                disabled={isRemovingMember}
              >
                {isRemovingMember ? 'Removendo...' : 'Remover'}
              </button>
            </div>
          </div>
        </>
      )}
    </main>
  )
}
