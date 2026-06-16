import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '@/app/providers/AuthContext'
import { useGetClassMembers } from '@/features/classes/hooks/useGetClassMembers'
import { useGetArtifacts } from '@/features/assignments/hooks/useGetArtifacts'
import { useAddArtifact } from '@/features/assignments/hooks/useAddArtifact'
import { useGetAssignment } from '@/features/assignments/hooks/useGetAssignment'
import { useGetClass } from '@/features/classes/hooks/useGetClass'
import { useCreateGroup } from '@/features/group/hooks/useCreateGroup'
import { useGetGroups } from '@/features/group/hooks/useGetGroups'
import { useJoinOpenGroup } from '@/features/group/hooks/useJoinOpenGroup'
import { useRequestGroupEntry } from '@/features/group/hooks/useRequestGroupEntry'
import { useCancelGroupEntryRequest } from '@/features/group/hooks/useCancelGroupEntryRequest'
import { useGetMyEntryRequests } from '@/features/group/hooks/useGetMyEntryRequests'
import { useGetGroupEntryRequests } from '@/features/group/hooks/useGetGroupEntryRequests'
import { useAcceptGroupEntryRequest } from '@/features/group/hooks/useAcceptGroupEntryRequest'
import { useRejectGroupEntryRequest } from '@/features/group/hooks/useRejectGroupEntryRequest'
import { useChangeGroupMode } from '@/features/group/hooks/useChangeGroupMode'
import { useDissolveGroup } from '@/features/group/hooks/useDissolveGroup'

import { AvatarMenu } from '@/components/ui/AvatarMenu'
import type { AssignmentArtifact } from '@/features/assignments/types/assignments.types'
import type { GroupEntryRequest } from '@/features/group/types/groups.types'

import styles from './AssignmentPage.module.css'

function MemberAvatar({ name }: { name: string }) {
  const avatarUrl = `https://api.dicebear.com/7.x/avataaars/svg?seed=${encodeURIComponent(name)}`

  return (
    <div className={styles.avatar} style={{ padding: 0, overflow: 'hidden' }}>
      <img src={avatarUrl} alt={name} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
    </div>
  )
}

export function AssignmentPage() {
  const navigate = useNavigate()
  const { id: courseId, assignmentId } = useParams<{ id: string; assignmentId: string }>()

  const { user } = useAuth()
  const { members: classMembers } = useGetClassMembers(courseId!)
  const { assignment, isLoading, isError } = useGetAssignment(courseId!, assignmentId!)
  const { course } = useGetClass(courseId!)
  const { create: createGroup, isLoading: isCreatingGroup } = useCreateGroup(courseId!, assignmentId!)
  const { groupsData, isLoading: isLoadingGroups } = useGetGroups(courseId!, assignmentId!)
  const { join, isLoading: isJoining } = useJoinOpenGroup(courseId!, assignmentId!)
  const { requestEntry, isLoading: isRequesting } = useRequestGroupEntry(courseId!, assignmentId!)
  const { cancel: cancelRequest, isLoading: isCancelling } = useCancelGroupEntryRequest(courseId!, assignmentId!)
  const { myRequests } = useGetMyEntryRequests(courseId!, assignmentId!)
  const { changeMode, isLoading: isChangingMode } = useChangeGroupMode(courseId!, assignmentId!)
  const { dissolve: dissolveGroup, isLoading: isDissolving } = useDissolveGroup(courseId!, assignmentId!)

  const [excludedRequestIds, setExcludedRequestIds] = useState<string[]>(() => {
    try {
      const userId = user?.id || 'anonymous'
      const stored = localStorage.getItem(`excluded-requests-${userId}-${assignmentId}`)
      return stored ? JSON.parse(stored) : []
    } catch {
      return []
    }
  })

  function handleExcludeRequest(requestId: string) {
    const next = [...excludedRequestIds, requestId]
    setExcludedRequestIds(next)
    try {
      const userId = user?.id || 'anonymous'
      localStorage.setItem(`excluded-requests-${userId}-${assignmentId}`, JSON.stringify(next))
    } catch {}
  }

  const visibleMyRequests = myRequests.filter((req) => !excludedRequestIds.includes(req.id))

  const myGroup = groupsData?.myGroup
  const isGroupLeaderOfClosedGroup =
    !!myGroup && myGroup.leaderId === user?.id && !myGroup.open

  const { requests: leaderPendingRequests } = useGetGroupEntryRequests(
    courseId!,
    assignmentId!,
    myGroup?.id,
    'PENDING'
  )

  const { accept: acceptRequest, isLoading: isAccepting } = useAcceptGroupEntryRequest(courseId!, assignmentId!)
  const { reject: rejectRequest, isLoading: isRejecting } = useRejectGroupEntryRequest(courseId!, assignmentId!)

  const { artifacts, isLoading: isLoadingArtifacts } = useGetArtifacts(courseId!, assignmentId!)
  const { add, isLoading: isAdding } = useAddArtifact(courseId!, assignmentId!)
  const [modalArtifact, setModalArtifact] = useState<AssignmentArtifact | null | 'new'>(null)
  const [formName, setFormName] = useState('')
  const [formDescription, setFormDescription] = useState('')
  const [formLink, setFormLink] = useState('')

  const [showCreateGroup, setShowCreateGroup] = useState(false)
  const [dissolveGroupId, setDissolveGroupId] = useState<string | null>(null)
  const [groupName, setGroupName] = useState('')
  const [groupOpen, setGroupOpen] = useState(true)

  const isOwner = course?.role === 'OWNER'
  const hasGroup = !!groupsData?.myGroup
  const canCreateGroup =
    assignment?.assignmentFlags.studentsCanCreateGroups === true &&
    !assignment?.isArchived

  const myRequestByGroupId = myRequests.reduce<Record<string, GroupEntryRequest>>((acc, req) => {
    if (req.status === 'PENDING' || req.status === 'REJECTED') {
      acc[req.groupId] = req
    }
    return acc
  }, {})

  const hasPendingRequest = myRequests.some((r) => r.status === 'PENDING')

  if (isLoading) return <div className={styles.feedback}>Carregando...</div>
  if (isError || !assignment) return <div className={styles.feedback}>Trabalho não encontrado.</div>

  const dueDate = assignment.dueDate
    ? new Date(assignment.dueDate).toLocaleDateString('pt-BR')
    : null

  const totalStudents = classMembers.length
  const studentsInGroups = groupsData?.groups.content.reduce((sum, g) => sum + g.memberCount, 0) ?? 0
  const studentsWithoutGroup = Math.max(0, totalStudents - studentsInGroups)

  const maxGroupMembers = assignment.assignmentFlags.maxGroupMembers
  const hasMemberLimit = maxGroupMembers !== 999
  const currentGroups = groupsData?.groups.totalElements ?? 0
  const maxGroupsCalculated = hasMemberLimit ? Math.ceil(totalStudents / maxGroupMembers) : null

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
    } catch {}
  }

  async function handleJoin(groupId: string) {
    try {
      await join(groupId)
    } catch {}
  }

  async function handleRequestEntry(groupId: string) {
    try {
      await requestEntry(groupId)
    } catch {
      // handled by hook
    }
  }

  async function handleCancelRequest(req: GroupEntryRequest) {
    try {
      await cancelRequest({ groupId: req.groupId, requestId: req.id })
    } catch {
      // handled by hook
    }
  }

  async function handleAcceptRequest(groupId: string, requestId: string) {
    try {
      await acceptRequest({ groupId, requestId })
    } catch {
      // handled by hook
    }
  }

  async function handleRejectRequest(groupId: string, requestId: string) {
    try {
      await rejectRequest({ groupId, requestId })
    } catch {
      // handled by hook
    }
  }

  async function handleToggleMode(groupId: string, currentOpen: boolean) {
    try {
      await changeMode({ groupId, open: !currentOpen })
    } catch {
      // handled by hook
    }
  }

  async function handleDissolve(groupId: string) {
    try {
      await dissolveGroup(groupId)
    } catch {
      // handled by hook
    }
  }

  const isAssignmentActive = !assignment.isArchived

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
              Adicionar artefatos
            </button>
          )}
        </div>

        {isOwner && (
          <>
            <div className={styles.supervisorStatsCard}>
              <div className={styles.statsRow}>
                <span className={styles.statsInGroups}>
                  {studentsInGroups} estudantes em grupos
                </span>
                <span className={styles.statsWithoutGroup}>
                  {studentsWithoutGroup} sem grupo
                </span>
              </div>
              <div className={styles.statsProgressBar}>
                <div
                  className={styles.statsProgressFillInGroups}
                  style={{ width: `${totalStudents > 0 ? (studentsInGroups / totalStudents) * 100 : 0}%` }}
                />
                <div
                  className={styles.statsProgressFillWithoutGroup}
                  style={{ width: `${totalStudents > 0 ? (studentsWithoutGroup / totalStudents) * 100 : 0}%` }}
                />
              </div>
            </div>
            <hr className={styles.sectionDivider} />
          </>
        )}

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
              <span>Grupos formados</span>
            </div>
            <span className={styles.groupsCount}>
              {currentGroups}/{hasMemberLimit ? maxGroupsCalculated : '∞'}
            </span>
          </div>
          <div className={`${styles.groupsProgressBar} ${!hasMemberLimit ? styles.inactiveGroupsProgressBar : ''}`}>
            {hasMemberLimit && (
              <div
                className={styles.groupsProgressFill}
                style={{
                  width: `${maxGroupsCalculated && maxGroupsCalculated > 0 ? Math.min(100, (currentGroups / maxGroupsCalculated) * 100) : 0}%`,
                }}
              />
            )}
          </div>

          <div className={styles.groupsList}>
            {isLoadingGroups && <p className={styles.feedbackSmall}>Carregando grupos...</p>}

            {!isLoadingGroups && (!groupsData || groupsData.groups.content.length === 0) && (
              <p className={styles.emptyGroups}>Nenhum grupo formado ainda</p>
            )}

            {!isLoadingGroups && groupsData && groupsData.groups.content.length > 0 && (
              <div className={styles.groupsGrid}>
                {[...groupsData.groups.content]
                  .sort((a, b) => {
                    const isAMyGroup = groupsData.myGroup?.id === a.id
                    const isBMyGroup = groupsData.myGroup?.id === b.id
                    if (isAMyGroup && !isBMyGroup) return -1
                    if (!isAMyGroup && isBMyGroup) return 1
                    return 0
                  })
                  .map((g) => {
                  const isMyGroup = groupsData.myGroup?.id === g.id
                  const maxMembers = assignment.assignmentFlags.maxGroupMembers
                  const isFull = g.memberCount >= maxMembers
                  const myRequest = myRequestByGroupId[g.id]

                  const canAct = !hasGroup && !isOwner && isAssignmentActive

                  const showModeBtn = isMyGroup && g.leaderId === user?.id && assignment.assignmentFlags.groupLeaderCanChangeMode && isAssignmentActive
                  const showDissolveBtn = ((isMyGroup && g.leaderId === user?.id && assignment.assignmentFlags.groupLeaderCanDissolve) ||
                    (isOwner && assignment.assignmentFlags.supervisorCanEditGroups)) && isAssignmentActive
                  const showActionButtons = showModeBtn || showDissolveBtn

                  return (
                    <div
                      key={g.id}
                      className={`${styles.groupCard} ${isMyGroup ? styles.myGroupCard : ''}`}
                    >
                      <div className={styles.groupCardTop}>
                        <div className={styles.groupCardHeaderLeft}>
                          <span className={styles.groupName}>{g.name}</span>
                          {showActionButtons && (
                            <div className={styles.groupMeta}>
                              <span className={`${styles.groupBadge} ${g.open ? styles.openBadge : styles.closedBadge}`}>
                                {g.open ? 'Aberto' : 'Fechado'}
                              </span>
                              {isMyGroup && <span className={styles.myGroupTag}>Meu grupo</span>}
                              {/* Request status badges */}
                              {!isMyGroup && myRequest?.status === 'PENDING' && (
                                <span className={styles.badgePending}>Pendente</span>
                              )}
                              {!isMyGroup && myRequest?.status === 'REJECTED' && (
                                <span className={styles.badgeRejected}>Rejeitada</span>
                              )}
                            </div>
                          )}
                        </div>

                        <div className={styles.groupCardHeaderRight}>
                          {showActionButtons ? (
                            <>
                              {showModeBtn && (
                                <button
                                  id={`toggle-group-mode-${g.id}`}
                                  className={styles.groupActionIconBtn}
                                  onClick={(e) => {
                                    e.stopPropagation()
                                    handleToggleMode(g.id, g.open)
                                  }}
                                  disabled={isChangingMode}
                                  title={g.open ? 'Fechar grupo' : 'Abrir grupo'}
                                >
                                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                                    {g.open ? (
                                      <>
                                        <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                                        <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                                      </>
                                    ) : (
                                      <>
                                        <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                                        <path d="M7 11V7a5 5 0 0 1 9.9-1" />
                                      </>
                                    )}
                                  </svg>
                                </button>
                              )}

                              {showDissolveBtn && (
                                <button
                                  id={`dissolve-group-${g.id}`}
                                  className={styles.groupActionDissolveBtn}
                                  onClick={(e) => {
                                    e.stopPropagation()
                                    setDissolveGroupId(g.id)
                                  }}
                                  disabled={isDissolving}
                                  title="Dissolver grupo"
                                >
                                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                                    <path d="M3 6h18M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2M10 11v6M14 11v6" />
                                  </svg>
                                </button>
                              )}
                            </>
                          ) : (
                            <div className={styles.groupMeta}>
                              <span className={`${styles.groupBadge} ${g.open ? styles.openBadge : styles.closedBadge}`}>
                                {g.open ? 'Aberto' : 'Fechado'}
                              </span>
                              {isMyGroup && <span className={styles.myGroupTag}>Meu grupo</span>}
                              {/* Request status badges */}
                              {!isMyGroup && myRequest?.status === 'PENDING' && (
                                <span className={styles.badgePending}>Pendente</span>
                              )}
                              {!isMyGroup && myRequest?.status === 'REJECTED' && (
                                <span className={styles.badgeRejected}>Rejeitada</span>
                              )}
                            </div>
                          )}
                        </div>
                      </div>

                      <hr className={styles.groupCardDivider} />

                      <div className={styles.groupCardMembersSection}>
                        <span className={styles.groupMembersLabel}>
                          Membros do grupo - <strong>{g.memberCount}/{maxMembers === 999 ? 'Sem limite' : maxMembers}</strong>
                        </span>
                        
                        <div className={styles.memberAvatarsList}>
                          {/* Render memberCount avatars */}
                          {Array.from({ length: g.memberCount }).map((_, index) => {
                            const seeds = ['custom1', 'custom2', 'custom3', 'custom4', 'custom5']
                            const seed = seeds[index % seeds.length] + '-' + g.id.slice(0, 4)
                            const avatarUrl = `https://api.dicebear.com/7.x/avataaars/svg?seed=${seed}`
                            return (
                              <img
                                key={index}
                                src={avatarUrl}
                                alt="Membro"
                                className={styles.memberAvatarImg}
                              />
                            )
                          })}
                          
                          {/* Render empty slots if there is a limit */}
                          {hasMemberLimit &&
                            Array.from({ length: Math.max(0, maxMembers - g.memberCount) }).map((_, index) => (
                              <div key={index} className={styles.emptyMemberSlot} />
                            ))}
                        </div>
                      </div>

                      {canAct && !isMyGroup && (
                        <div className={styles.groupCardActionsBottom}>
                          {g.open && !isFull && (
                            <button
                              id={`join-group-${g.id}`}
                              className={styles.joinBtn}
                              onClick={() => handleJoin(g.id)}
                              disabled={isJoining}
                            >
                              Entrar
                            </button>
                          )}

                          {!g.open && !isFull && !myRequest && !hasPendingRequest && (
                            <button
                              id={`request-entry-${g.id}`}
                              className={styles.requestBtn}
                              onClick={() => handleRequestEntry(g.id)}
                              disabled={isRequesting}
                            >
                              Solicitar
                            </button>
                          )}

                          {!g.open && myRequest?.status === 'PENDING' && (
                            <button
                              id={`cancel-request-${g.id}`}
                              className={styles.cancelRequestBtn}
                              onClick={() => handleCancelRequest(myRequest)}
                              disabled={isCancelling}
                            >
                              Cancelar
                            </button>
                          )}
                        </div>
                      )}
                    </div>
                  )
                })}
              </div>
            )}
          </div>

          {canCreateGroup && (
            <button
              className={styles.createGroupBtn}
              onClick={openCreateGroup}
              disabled={hasGroup && !isOwner}
            >
              Criar grupo
            </button>
          )}
        </div>

        {isGroupLeaderOfClosedGroup && leaderPendingRequests.length > 0 && (
          <div className={styles.leaderRequestsSection}>
            <p className={styles.leaderRequestsTitle}>Solicitações de entrada</p>
            <div className={styles.leaderRequestsList}>
              {leaderPendingRequests.map((req) => {
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
                        onClick={() => handleAcceptRequest(myGroup.id, req.id)}
                        disabled={isAccepting || isRejecting}
                      >
                        {isAccepting ? 'Aceitando...' : 'Aceitar'}
                      </button>
                      <button
                        id={`reject-request-${req.id}`}
                        className={styles.rejectBtn}
                        onClick={() => handleRejectRequest(myGroup.id, req.id)}
                        disabled={isAccepting || isRejecting}
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

        {visibleMyRequests.length > 0 && (
          <div className={styles.myRequestsSection}>
            <p className={styles.myRequestsTitle}>Minhas solicitações</p>
            <div className={styles.myRequestsList}>
              {visibleMyRequests.map((req) => {
                const group = groupsData?.groups.content.find((g) => g.id === req.groupId)
                const groupName = group?.name ?? 'Grupo removido'
                return (
                  <div
                    key={req.id}
                    className={`${styles.requestCard} ${
                      req.status === 'PENDING'
                        ? styles.requestCardPending
                        : req.status === 'REJECTED'
                          ? styles.requestCardRejected
                          : styles.requestCardAccepted
                    }`}
                  >
                    <div className={styles.requestCardInfo}>
                      <span className={styles.requestCardGroupName}>{groupName}</span>
                      <span className={styles.requestCardStatus}>
                        {req.status === 'PENDING' && 'Aguardando resposta'}
                        {req.status === 'REJECTED' && 'Solicitação rejeitada'}
                        {req.status === 'ACCEPTED' && 'Solicitação aceita'}
                      </span>
                    </div>
                    {req.status === 'PENDING' && (
                      <button
                        className={styles.cancelRequestInlineBtn}
                        onClick={() => handleCancelRequest(req)}
                        disabled={isCancelling}
                      >
                        Cancelar
                      </button>
                    )}
                    {(req.status === 'ACCEPTED' || req.status === 'REJECTED') && (
                      <button
                        id={`exclude-request-${req.id}`}
                        className={styles.excludeRequestBtn}
                        onClick={() => handleExcludeRequest(req.id)}
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
                          <path d="M18 6L6 18M6 6l12 12" />
                        </svg>
                      </button>
                    )}
                  </div>
                )
              })}
            </div>
          </div>
        )}
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

      {dissolveGroupId !== null && (
        <>
          <div className={styles.overlay} onClick={() => setDissolveGroupId(null)} />
          <div className={styles.confirmModal}>
            <button className={styles.confirmModalCloseBtn} onClick={() => setDissolveGroupId(null)}>
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

            <p className={styles.confirmModalTitle}>Dissolver grupo</p>
            
            <p className={styles.confirmModalWarning}>
              Tem certeza que deseja dissolver este grupo? Todos os membros serão removidos.
            </p>

            <div className={styles.confirmModalActions}>
              <button className={styles.confirmModalCancelBtn} onClick={() => setDissolveGroupId(null)}>
                Cancelar
              </button>
              <button
                className={styles.confirmModalConfirmBtn}
                onClick={async () => {
                  if (dissolveGroupId) {
                    await handleDissolve(dissolveGroupId)
                    setDissolveGroupId(null)
                  }
                }}
                disabled={isDissolving}
              >
                {isDissolving ? 'Dissolvendo...' : 'Dissolver'}
              </button>
            </div>
          </div>
        </>
      )}
    </main>
  )
}
