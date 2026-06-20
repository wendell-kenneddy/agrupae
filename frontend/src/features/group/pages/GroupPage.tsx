import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useQueries } from '@tanstack/react-query'
import { getGroupMembers } from '@/features/group/api/groupsApi'
import { NotFoundPage } from '@/components/ui/NotFoundPage'
import { LoadingScreen } from '@/components/ui/LoadingScreen'
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
import { useEditGroup } from '@/features/group/hooks/useEditGroup'
import { useEditGroupArtifact } from '@/features/group/hooks/useEditGroupArtifact'
import type { GroupArtifact } from '@/features/group/types/groups.types'
import { useDissolveGroup } from '@/features/group/hooks/useDissolveGroup'
import { useJoinOpenGroup } from '@/features/group/hooks/useJoinOpenGroup'
import { useRequestGroupEntry } from '@/features/group/hooks/useRequestGroupEntry'
import { useCancelGroupEntryRequest } from '@/features/group/hooks/useCancelGroupEntryRequest'
import { useGetMyEntryRequests } from '@/features/group/hooks/useGetMyEntryRequests'
import { useGetGroupEntryRequests } from '@/features/group/hooks/useGetGroupEntryRequests'
import { useAcceptGroupEntryRequest } from '@/features/group/hooks/useAcceptGroupEntryRequest'
import { useRejectGroupEntryRequest } from '@/features/group/hooks/useRejectGroupEntryRequest'
import { useGetClassMembers } from '@/features/classes/hooks/useGetClassMembers'
import { AvatarMenu } from '@/components/ui/AvatarMenu'
import styles from './GroupPage.module.css'


import { UserAvatar } from '@/components/ui/UserAvatar'

function MemberAvatar({ name }: { name: string }) {
  return <UserAvatar name={name} size="md" />
}

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
  const { editArtifact, isLoading: isEditingArtifact } = useEditGroupArtifact(courseId!, assignmentId!, groupId!)
  const { toggleDeliverable, isLoading: isTogglingDeliverable } = useToggleGroupArtifactDeliverable(courseId!, assignmentId!, groupId!)

  const { leave: leaveGroup, isLoading: isLeaving } = useLeaveGroup(courseId!, assignmentId!)
  const { removeMember, isLoading: isRemovingMember } = useRemoveGroupMember(courseId!, assignmentId!)
  const { changeMode, isLoading: isChangingMode } = useChangeGroupMode(courseId!, assignmentId!)
  const { edit: editGroup, isLoading: isEditingGroup } = useEditGroup(courseId!, assignmentId!)
  const { dissolve: dissolveGroup, isLoading: isDissolving } = useDissolveGroup(courseId!, assignmentId!)

  const { join, isLoading: isJoining } = useJoinOpenGroup(courseId!, assignmentId!)
  const { requestEntry, isLoading: isRequesting } = useRequestGroupEntry(courseId!, assignmentId!)
  const { cancel: cancelRequest, isLoading: isCancelling } = useCancelGroupEntryRequest(courseId!, assignmentId!)
  const { myRequests } = useGetMyEntryRequests(courseId!, assignmentId!)

  const [showAddLink, setShowAddLink] = useState(false)
  const [linkName, setLinkName] = useState('')
  const [linkUrl, setLinkUrl] = useState('')
  const [linkDescription, setLinkDescription] = useState('')

  const [editingArtifact, setEditingArtifact] = useState<GroupArtifact | null>(null)
  const [editArtifactName, setEditArtifactName] = useState('')
  const [editArtifactUrl, setEditArtifactUrl] = useState('')
  const [editArtifactDescription, setEditArtifactDescription] = useState('')
  const [artifactToDelete, setArtifactToDelete] = useState<GroupArtifact | null>(null)

  const [showMenu, setShowMenu] = useState(false)
  const [showEditGroupModal, setShowEditGroupModal] = useState(false)
  const [editGroupName, setEditGroupName] = useState('')
  const [editGroupOpen, setEditGroupOpen] = useState(true)

  // Confirm actions states
  const [showConfirmLeave, setShowConfirmLeave] = useState(false)
  const [showConfirmDissolve, setShowConfirmDissolve] = useState(false)
  const [removeMemberInfo, setRemoveMemberInfo] = useState<{ id: string; name: string } | null>(null)

  const isOwner = course?.role === 'OWNER'
  const isAssignmentActive = assignment ? !assignment.isArchived : false
  const isGroupLeader = group ? group.leaderId === user?.id : false

  const { requests: leaderPendingRequests = [] } = useGetGroupEntryRequests(
    courseId!,
    assignmentId!,
    isGroupLeader ? groupId : undefined,
    'PENDING'
  )
  const { accept: acceptRequest, isLoading: isAccepting } = useAcceptGroupEntryRequest(courseId!, assignmentId!)
  const { reject: rejectRequest, isLoading: isRejecting } = useRejectGroupEntryRequest(courseId!, assignmentId!)
  const { members: classMembers } = useGetClassMembers(courseId!)

  const [failedRequestIds, setFailedRequestIds] = useState<string[]>([])

  const groupIds = groupsData?.groups.content.map((g) => g.id) ?? []

  const groupMembersQueries = useQueries({
    queries: groupIds.map((id) => ({
      queryKey: ['group-members', courseId, assignmentId, id],
      queryFn: () => getGroupMembers(courseId!, assignmentId!, id),
      enabled: !!courseId && !!assignmentId && !!id,
    })),
  })

  const membersInGroupsIds = new Set<string>()
  groupMembersQueries.forEach((q) => {
    if (q.data?.content) {
      q.data.content.forEach((m) => {
        membersInGroupsIds.add(m.id)
      })
    }
  })

  const visibleLeaderRequests = leaderPendingRequests.filter(
    (req) => !failedRequestIds.includes(req.id) && !membersInGroupsIds.has(req.userId)
  )

  async function handleAcceptRequest(requestId: string) {
    if (!assignment || assignment.isArchived) return
    try {
      await acceptRequest({ groupId: groupId!, requestId })
    } catch {
      setFailedRequestIds((prev) => [...prev, requestId])
    }
  }

  async function handleRejectRequest(requestId: string) {
    if (!assignment || assignment.isArchived) return
    try {
      await rejectRequest({ groupId: groupId!, requestId })
    } catch {
      // handled by hook
    }
  }

  useEffect(() => {
    if (!isLoadingAssignment && !isLoadingGroups && !isLoadingMembers) {
      if (!isMember && !isOwner) {
        navigate(`/classes/${courseId}/assignments/${assignmentId}`)
      }
    }
  }, [isLoadingAssignment, isLoadingGroups, isLoadingMembers, isMember, isOwner, navigate, courseId, assignmentId])

  if (isLoadingAssignment || isLoadingGroups || isLoadingMembers) {
    return <LoadingScreen />
  }

  if (!group || !assignment || (!isMember && !isOwner)) {
    return <NotFoundPage />
  }

  const myRequest = myRequests.find((r) => r.groupId === groupId && (r.status === 'PENDING' || r.status === 'REJECTED'))
  const hasPendingRequest = myRequests.some((r) => r.status === 'PENDING')

  const maxMembers = assignment.assignmentFlags.maxGroupMembers
  const isFull = group.memberCount >= maxMembers

  const showLeaveOption = isMember && assignment.assignmentFlags.studentsCanLeaveGroups && isAssignmentActive
  const showEditGroupOption = isGroupLeader && isAssignmentActive
  const canChangeMode = assignment.assignmentFlags.groupLeaderCanChangeMode
  const showDissolveOption =
    ((isGroupLeader && assignment.assignmentFlags.groupLeaderCanDissolve) ||
      (isOwner && assignment.assignmentFlags.supervisorCanEditGroups)) &&
    isAssignmentActive

  const hasMenuOptions = showLeaveOption || showEditGroupOption || showDissolveOption

  async function handleAddLink() {
    if (!assignment || assignment.isArchived) return
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

  function openEditArtifact(a: GroupArtifact) {
    if (!assignment || assignment.isArchived) return
    setEditingArtifact(a)
    setEditArtifactName(a.name)
    setEditArtifactDescription(a.description || '')
    setEditArtifactUrl(a.resourceLink)
  }

  async function handleEditLink() {
    if (!assignment || assignment.isArchived) return
    if (!editingArtifact || !editArtifactName.trim() || !editArtifactUrl.trim()) return
    try {
      await editArtifact({
        artifactId: editingArtifact.id,
        data: {
          name: editArtifactName.trim(),
          resourceLink: editArtifactUrl.trim(),
          description: editArtifactDescription.trim(),
        },
      })
      setEditingArtifact(null)
    } catch {}
  }

  async function handleConfirmDeleteArtifact() {
    if (!assignment || assignment.isArchived) return
    if (!artifactToDelete) return
    try {
      await deleteArtifact(artifactToDelete.id)
      setArtifactToDelete(null)
    } catch {}
  }

  async function handleLeave() {
    if (!assignment || assignment.isArchived) return
    try {
      await leaveGroup(groupId!)
      setShowConfirmLeave(false)
      navigate(`/classes/${courseId}/assignments/${assignmentId}`)
    } catch {}
  }

  async function handleDissolve() {
    if (!assignment || assignment.isArchived) return
    try {
      await dissolveGroup(groupId!)
      setShowConfirmDissolve(false)
      navigate(`/classes/${courseId}/assignments/${assignmentId}`)
    } catch {}
  }

  function openEditGroup() {
    if (!assignment || assignment.isArchived) return
    if (group) {
      setEditGroupName(group.name)
      setEditGroupOpen(group.open)
      setShowMenu(false)
      setShowEditGroupModal(true)
    }
  }

  async function handleEditGroup() {
    if (!assignment || assignment.isArchived) return
    if (!editGroupName.trim() || !group) return
    try {
      if (editGroupName.trim() !== group.name) {
        await editGroup({ groupId: groupId!, name: editGroupName.trim() })
      }
      if (editGroupOpen !== group.open) {
        await changeMode({ groupId: groupId!, open: editGroupOpen })
      }
      setShowEditGroupModal(false)
    } catch {}
  }


  async function handleRemove(memberId: string) {
    if (!assignment || assignment.isArchived) return
    try {
      await removeMember({ groupId: groupId!, memberId })
      setRemoveMemberInfo(null)
    } catch {}
  }

  async function handleJoinGroup() {
    if (!assignment || assignment.isArchived) return
    try {
      await join(groupId!)
    } catch {}
  }

  async function handleRequestGroupEntry() {
    if (!assignment || assignment.isArchived) return
    try {
      await requestEntry(groupId!)
    } catch {}
  }

  async function handleCancelRequestEntry() {
    if (!assignment || assignment.isArchived) return
    if (myRequest && myRequest.status === 'PENDING') {
      try {
        await cancelRequest({ groupId: groupId!, requestId: myRequest.id })
      } catch {}
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
          {!isAssignmentActive && (
            <div className={styles.archivedWarningCard}>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                <polyline points="21 8 21 21 3 21 3 8" />
                <rect x="1" y="3" width="22" height="5" />
                <line x1="10" y1="12" x2="14" y2="12" />
              </svg>
              <span>Este trabalho está arquivado. As interações estão desativadas.</span>
            </div>
          )}

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
                  {showEditGroupOption && (
                    <button className={styles.dropdownItem} onClick={openEditGroup}>
                      Editar grupo
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

            <div className={styles.linksList}>
              {isLoadingArtifacts && <div style={{ fontSize: '0.8rem', color: '#1e3a8a' }}>Carregando links...</div>}
              {!isLoadingArtifacts && artifacts.length === 0 && (
                <div style={{ fontSize: '0.8rem', color: '#1e3a8a', opacity: 0.7 }}>Nenhum link adicionado ainda.</div>
              )}
              {!isLoadingArtifacts &&
                artifacts.map((a) => (
                  <div
                    key={a.id}
                    className={`${styles.linkRow} ${a.deliverable ? styles.deliverableRow : ''}`}
                  >
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
                      <span className={styles.linkInfo}>
                        {a.name}
                        {a.description ? ` · ${a.description}` : ''}
                      </span>
                      {a.deliverable && (
                        <span className={styles.deliverableBadge}>Entregável</span>
                      )}
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
                          className={styles.linkExportBtn}
                          onClick={() => openEditArtifact(a)}
                          title="Editar link"
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
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                          </svg>
                        </button>
                        <button
                          className={styles.linkDeleteBtn}
                          onClick={() => setArtifactToDelete(a)}
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

          {/* Action buttons for non-members */}
          {!isMember && !hasGroup && isAssignmentActive && (
            <div style={{ marginTop: 0 }}>
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
                  style={{ background: 'var(--color-orange)' }}
                  onClick={handleCancelRequestEntry}
                  disabled={isCancelling}
                >
                  {isCancelling ? 'Cancelando...' : 'Cancelar solicitação'}
                </button>
              )}
              {myRequest?.status === 'REJECTED' && (
                <div style={{ textAlign: 'center', fontSize: '0.85rem', color: 'var(--color-red)', fontWeight: 600, padding: 8 }}>
                  Sua solicitação de entrada foi rejeitada.
                </div>
              )}
            </div>
          )}

          <div className={styles.membersSection}>
            <h2 className={styles.sectionTitle}>Membros</h2>

            <div className={styles.memberList}>
              {members.map((member) => (
                <div key={member.id} className={styles.memberRow}>
                  <div className={styles.memberLeft}>
                    <UserAvatar
                      name={member.name}
                      className={styles.memberAvatar}
                    />
                    <div className={styles.memberInfo}>
                      <span className={styles.memberName}>
                        {member.id === user?.id ? 'Você' : member.name}
                        {member.isLeader && <span className={styles.leaderBadge}>Líder</span>}
                      </span>
                      <span className={styles.memberEmail}>{member.email}</span>
                    </div>
                  </div>

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

          {isGroupLeader && visibleLeaderRequests.length > 0 && isAssignmentActive && (
            <div className={styles.leaderRequestsSection}>
              <p className={styles.leaderRequestsTitle}>Solicitações de entrada</p>
              <div className={styles.leaderRequestsList}>
                {visibleLeaderRequests.map((req) => {
                  const requester = classMembers.find((m) => m.id === req.userId)
                  const requesterName = requester?.name ?? 'Estudante'
                  const requesterEmail = requester?.email ?? ''

                  return (
                    <div key={req.id} className={styles.leaderRequestCard}>
                      <div className={styles.leaderRequestLeft}>
                        <MemberAvatar name={requesterName} />
                        <div className={styles.leaderRequestInfo}>
                          <span className={styles.leaderRequestName}>{requesterName}</span>
                          {requesterEmail && (
                            <span className={styles.leaderRequestEmail}>{requesterEmail}</span>
                          )}
                        </div>
                      </div>
                      <div className={styles.leaderRequestActions}>
                        <button
                          id={`accept-request-${req.id}`}
                          className={styles.acceptBtn}
                          onClick={() => handleAcceptRequest(req.id)}
                          disabled={isAccepting || isRejecting || !isAssignmentActive}
                        >
                          {isAccepting ? 'Aceitando...' : 'Aceitar'}
                        </button>
                        <button
                          id={`reject-request-${req.id}`}
                          className={styles.rejectBtn}
                          onClick={() => handleRejectRequest(req.id)}
                          disabled={isAccepting || isRejecting || !isAssignmentActive}
                        >
                          {isRejecting ? 'Rejeitando...' : 'Rejeitar'}
                        </button>
                      </div>
                    </div>
                  )
                })}
              </div>
            </div>
          )}
        </div>

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

      {showEditGroupModal && (
        <>
          <div className={styles.overlay} onClick={() => setShowEditGroupModal(false)} />
          <div className={styles.modal}>
            <button className={styles.closeBtn} onClick={() => setShowEditGroupModal(false)}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M18 6L6 18M6 6l12 12" />
              </svg>
            </button>

            <p className={styles.modalTitle}>Editar grupo</p>

            <div className={styles.modalFields}>
              <input
                className={styles.modalInput}
                type="text"
                placeholder="Nome do grupo"
                value={editGroupName}
                onChange={(e) => setEditGroupName(e.target.value)}
              />

              {canChangeMode && (
                <div className={styles.modeToggle}>
                  <span className={styles.modeLabel}>Modo do grupo</span>
                  <div className={styles.modeOptions}>
                    <button
                      className={`${styles.modeOption} ${editGroupOpen ? styles.modeOptionActive : ''}`}
                      onClick={() => setEditGroupOpen(true)}
                      type="button"
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                        <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                        <path d="M7 11V7a5 5 0 0 1 9.9-1" />
                      </svg>
                      Aberto
                    </button>
                    <button
                      className={`${styles.modeOption} ${!editGroupOpen ? styles.modeOptionActive : ''}`}
                      onClick={() => setEditGroupOpen(false)}
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
                    {editGroupOpen
                      ? 'Qualquer estudante da turma pode entrar diretamente.'
                      : 'Estudantes precisam solicitar entrada. Você aprova ou rejeita.'}
                  </span>
                </div>
              )}
            </div>

            <div className={styles.modalActions}>
              <button className={styles.cancelBtn} onClick={() => setShowEditGroupModal(false)}>
                Cancelar
              </button>
              <button
                className={styles.confirmBtn}
                onClick={handleEditGroup}
                disabled={!editGroupName.trim() || isEditingGroup || isChangingMode}
              >
                {isEditingGroup || isChangingMode ? 'Salvando...' : 'Salvar'}
              </button>
            </div>
          </div>
        </>
      )}

      {/* Edit Group Artifact Modal */}
      {editingArtifact !== null && (
        <>
          <div className={styles.overlay} onClick={() => setEditingArtifact(null)} />
          <div className={styles.modal}>
            <button className={styles.closeBtn} onClick={() => setEditingArtifact(null)}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M18 6L6 18M6 6l12 12" />
              </svg>
            </button>

            <p className={styles.modalTitle}>Editar artefato</p>

            <div className={styles.modalFields}>
              <input
                className={styles.modalInput}
                type="text"
                placeholder="Nome"
                value={editArtifactName}
                onChange={(e) => setEditArtifactName(e.target.value)}
              />
              <input
                className={styles.modalInput}
                type="text"
                placeholder="Descrição (opcional)"
                value={editArtifactDescription}
                onChange={(e) => setEditArtifactDescription(e.target.value)}
              />
              <input
                className={styles.modalInput}
                type="url"
                placeholder="Link"
                value={editArtifactUrl}
                onChange={(e) => setEditArtifactUrl(e.target.value)}
              />
            </div>

            <div className={styles.modalActions}>
              <button className={styles.cancelBtn} onClick={() => setEditingArtifact(null)}>
                Cancelar
              </button>
              <button
                className={styles.confirmBtn}
                onClick={handleEditLink}
                disabled={!editArtifactName.trim() || !editArtifactUrl.trim() || isEditingArtifact}
              >
                {isEditingArtifact ? 'Salvando...' : 'Salvar'}
              </button>
            </div>
          </div>
        </>
      )}

      {/* Delete Group Artifact Confirmation Modal */}
      {artifactToDelete !== null && (
        <>
          <div className={styles.overlay} onClick={() => setArtifactToDelete(null)} />
          <div className={styles.confirmModal}>
            <button className={styles.confirmModalCloseBtn} onClick={() => setArtifactToDelete(null)}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M18 6L6 18M6 6l12 12" />
              </svg>
            </button>

            <p className={styles.confirmModalTitle}>Excluir Link</p>
            <p className={styles.confirmModalSub} style={{ margin: '12px 24px 20px', color: 'var(--color-text-dark)', lineHeight: '1.4' }}>
              Tem certeza que deseja excluir o link <strong>{artifactToDelete.name}</strong>? Esta ação não pode ser desfeita.
            </p>

            <div className={styles.confirmModalActions}>
              <button
                className={styles.confirmModalCancelBtn}
                onClick={() => setArtifactToDelete(null)}
                disabled={isDeletingArtifact}
              >
                Cancelar
              </button>
              <button
                className={styles.confirmModalConfirmBtn}
                onClick={handleConfirmDeleteArtifact}
                disabled={isDeletingArtifact}
              >
                {isDeletingArtifact ? 'Excluindo...' : 'Excluir'}
              </button>
            </div>
          </div>
        </>
      )}
    </main>
  )
}
