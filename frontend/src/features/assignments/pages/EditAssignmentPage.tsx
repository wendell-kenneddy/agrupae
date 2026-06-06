import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { useEditAssignment } from '@/features/assignments/hooks/useEditAssignment'
// import { assignmentsMock } from '@/features/assignments/mocks/assignments.mock'
import { PRESETS } from '@/features/assignments/types/assignments.types'
import type {
  Assignment,
  AssignmentFlags,
  AssignmentMode,
} from '@/features/assignments/types/assignments.types'

import styles from './CreateAssignmentPage.module.css'

const FLAG_LABELS: Record<
  keyof Omit<AssignmentFlags, 'maxGroupMembers' | 'maxGroups'>,
  { label: string; description: string }
> = {
  studentsCanCreateGroups: {
    label: 'Estudantes criam grupos',
    description: 'Permite que estudantes criem novos grupos',
  },
  studentsCanLeaveGroups: {
    label: 'Estudantes podem sair',
    description: 'Permite saída voluntária do grupo',
  },
  groupLeaderCanDissolve: {
    label: 'Líder pode dissolver o grupo',
    description: 'Líder encerra o grupo e libera os membros',
  },
  groupLeaderCanRemoveMembers: {
    label: 'Líder pode remover membros',
    description: 'Líder remove integrantes individualmente',
  },
  groupLeaderCanChangeMode: {
    label: 'Líder pode alterar o modo',
    description: 'Líder alterna entre grupo aberto e fechado',
  },
  groupLeaderCanTransferLeadership: {
    label: 'Líder pode transferir liderança',
    description: 'Líder pode passar a liderança a outro membro',
  },
  supervisorCanEditGroups: {
    label: 'Responsável edita composição',
    description: 'Responsável adiciona ou remove membros diretamente',
  },
}

function getValidationError(
  flags: Omit<AssignmentFlags, 'maxGroupMembers' | 'maxGroups'>,
  mode: AssignmentMode
): { type: 'error' | 'warning'; message: string } | null {
  if (mode !== 'advanced') return null

  if (!flags.studentsCanCreateGroups && !flags.supervisorCanEditGroups) {
    return {
      type: 'error',
      message:
        "Nenhum ator pode criar grupos. Ative 'Estudantes criam grupos' ou 'Responsável edita composição' para continuar.",
    }
  }
  if (!flags.studentsCanLeaveGroups && flags.groupLeaderCanDissolve) {
    return {
      type: 'error',
      message:
        'Dissolução é uma saída forçada coletiva. Contradiz a restrição de saída voluntária.',
    }
  }
  if (
    flags.studentsCanCreateGroups &&
    flags.supervisorCanEditGroups &&
    !flags.studentsCanLeaveGroups
  ) {
    return {
      type: 'warning',
      message:
        "Autoridade sobre composição compartilhada com saída bloqueada. Verifique se 'Estudantes podem sair' deve permanecer desativado.",
    }
  }
  if (
    flags.groupLeaderCanDissolve &&
    !flags.studentsCanCreateGroups &&
    !flags.supervisorCanEditGroups
  ) {
    return {
      type: 'warning',
      message:
        'O líder pode dissolver grupos, mas nenhum novo grupo pode ser criado. Membros dissolvidos ficarão permanentemente sem grupo.',
    }
  }
  return null
}

function detectMode(flags: Omit<AssignmentFlags, 'maxGroupMembers' | 'maxGroups'>): AssignmentMode {
  for (const [mode, preset] of Object.entries(PRESETS) as [
    Exclude<AssignmentMode, 'advanced'>,
    (typeof PRESETS)[keyof typeof PRESETS],
  ][]) {
    if (Object.entries(preset).every(([k, v]) => flags[k as keyof typeof preset] === v)) {
      return mode
    }
  }
  return 'advanced'
}

export function EditAssignmentPage() {
  const navigate = useNavigate()
  const { id: courseId, assignmentId } = useParams<{ id: string; assignmentId: string }>()
  const { edit, isLoading } = useEditAssignment(courseId!, assignmentId!)

  const queryClient = useQueryClient()
  const assignment = queryClient
    .getQueryData<Assignment[]>(['assignments', courseId])
    ?.find((a) => a.id === assignmentId)

  if (!assignment) return <div>Trabalho não encontrado.</div>

  const { maxGroupMembers, maxGroups, ...flagsOnly } = assignment.assignmentFlags

  const [name, setName] = useState(assignment.name)
  const [description, setDescription] = useState(assignment.description ?? '')
  const [dueDate, setDueDate] = useState(
    assignment.dueDate ? new Date(assignment.dueDate).toISOString().split('T')[0] : ''
  )
  const [maxGroupMembersState, setMaxGroupMembers] = useState(
    maxGroupMembers === 999 ? 4 : maxGroupMembers
  )
  const [noLimit, setNoLimit] = useState(maxGroupMembers === 999)
  const [flags, setFlags] =
    useState<Omit<AssignmentFlags, 'maxGroupMembers' | 'maxGroups'>>(flagsOnly)
  const [forcedAdvanced, setForcedAdvanced] = useState(detectMode(flagsOnly) === 'advanced')

  const mode = forcedAdvanced ? 'advanced' : detectMode(flags)
  const validation = getValidationError(flags, mode)
  const isInvalid = validation?.type === 'error'

  function applyPreset(preset: Exclude<AssignmentMode, 'advanced'>) {
    setFlags(PRESETS[preset])
  }

  function toggleFlag(key: keyof Omit<AssignmentFlags, 'maxGroupMembers' | 'maxGroups'>) {
    setFlags((prev) => ({ ...prev, [key]: !prev[key] }))
  }

  async function handleSubmit() {
    if (!name.trim() || isInvalid) return
    await edit({
      name: name.trim(),
      description: description.trim() || 'Sem descrição',
      dueDate: dueDate ? new Date(dueDate).toISOString() : new Date('2099-12-31').toISOString(),
      assignmentFlags: {
        ...flags,
        maxGroupMembers: noLimit ? 999 : maxGroupMembersState,
        maxGroups: 999,
      },
    })
    navigate(-1)
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
        <h1 className={styles.title}>Editar trabalho</h1>
      </header>

      <div className={styles.content}>
        <div className={styles.field}>
          <label className={styles.label}>Nome do trabalho</label>
          <input
            className={styles.input}
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>

        <div className={styles.field}>
          <label className={styles.label}>
            Descrição <span className={styles.optional}>(opcional)</span>
          </label>
          <textarea
            className={styles.textarea}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={4}
          />
        </div>

        <div className={styles.field}>
          <label className={styles.label}>
            Data de entrega <span className={styles.optional}>(opcional)</span>
          </label>
          <input
            className={styles.input}
            type="date"
            value={dueDate}
            onChange={(e) => setDueDate(e.target.value)}
          />
        </div>

        <div className={styles.field}>
          <label className={styles.label}>Limite de membros por grupo</label>
          <div className={styles.counter}>
            <button
              className={styles.counterBtn}
              onClick={() => setMaxGroupMembers((v) => Math.max(1, v - 1))}
              disabled={noLimit}
            >
              −
            </button>
            <span className={styles.counterValue}>{noLimit ? '∞' : maxGroupMembersState}</span>
            <button
              className={styles.counterBtn}
              onClick={() => setMaxGroupMembers((v) => v + 1)}
              disabled={noLimit}
            >
              +
            </button>
          </div>
          <label className={styles.toggleRow}>
            <div
              className={`${styles.toggle} ${noLimit ? styles.toggleOn : ''}`}
              onClick={() => setNoLimit((v) => !v)}
            />
            <span className={styles.toggleLabel}>Sem limite de membros</span>
          </label>
        </div>

        <div className={styles.field}>
          <label className={styles.label}>Modo do trabalho</label>
          <p className={styles.hint}>Define como os grupos serão formados</p>
          <div className={styles.modeGrid}>
            {(['free', 'moderate', 'controlled', 'advanced'] as AssignmentMode[]).map((m) => (
              <button
                key={m}
                className={`${styles.modeCard} ${mode === m ? styles.modeCardActive : ''}`}
                onClick={() => {
                  if (m === 'advanced') {
                    setForcedAdvanced(true)
                  } else {
                    setForcedAdvanced(false)
                    applyPreset(m as Exclude<AssignmentMode, 'advanced'>)
                  }
                }}
              >
                <span className={styles.modeName}>
                  {m === 'free'
                    ? 'Livre'
                    : m === 'moderate'
                      ? 'Moderado'
                      : m === 'controlled'
                        ? 'Controlado'
                        : 'Avançado'}
                </span>
                <span className={styles.modeDesc}>
                  {m === 'free'
                    ? 'Estudantes criam e gerenciam os grupos'
                    : m === 'moderate'
                      ? 'Responsável estrutura, estudantes entram'
                      : m === 'controlled'
                        ? 'Responsável define e mantém a composição'
                        : 'Configure cada permissão individualmente'}
                </span>
              </button>
            ))}
          </div>
        </div>

        {mode === 'advanced' && (
          <div className={styles.field}>
            <label className={styles.label}>Personalizar permissões</label>
            <p className={styles.hint}>
              Configure as permissões do trabalho. Algumas combinações são inválidas ou exigem
              atenção.
            </p>
            <div className={styles.flagsList}>
              {(Object.keys(FLAG_LABELS) as (keyof typeof FLAG_LABELS)[]).map((key) => (
                <label key={key} className={styles.flagItem}>
                  <div
                    className={`${styles.toggle} ${flags[key] ? styles.toggleOn : ''}`}
                    onClick={() => toggleFlag(key)}
                  />
                  <div className={styles.flagText}>
                    <span className={styles.flagLabel}>{FLAG_LABELS[key].label}</span>
                    <span className={styles.flagDesc}>{FLAG_LABELS[key].description}</span>
                  </div>
                </label>
              ))}
            </div>
          </div>
        )}

        {validation && (
          <div
            className={`${styles.alert} ${validation.type === 'error' ? styles.alertError : styles.alertWarning}`}
          >
            <svg
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2.5"
              strokeLinecap="round"
            >
              {validation.type === 'error' ? (
                <>
                  <rect x="3" y="11" width="18" height="11" rx="2" />
                  <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                </>
              ) : (
                <>
                  <circle cx="12" cy="12" r="10" />
                  <path d="M12 8v4M12 16h.01" />
                </>
              )}
            </svg>
            <p>{validation.message}</p>
          </div>
        )}
      </div>

      <div className={styles.footer}>
        <button
          className={styles.submitBtn}
          onClick={handleSubmit}
          disabled={!name.trim() || isInvalid || isLoading}
        >
          {isLoading ? 'Salvando...' : 'Salvar alterações'}
        </button>
      </div>
    </main>
  )
}
