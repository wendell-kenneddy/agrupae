import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useCreateAssignment } from '@/features/assignments/hooks/useCreateAssignment'
import { PRESETS } from '@/features/assignments/types/assignments.types'
import type {
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

function getValidationErrors(
  flags: Omit<AssignmentFlags, 'maxGroupMembers' | 'maxGroups'>,
  mode: AssignmentMode
): { type: 'error' | 'warning'; message: string }[] {
  if (mode !== 'advanced') return []

  const results: { type: 'error' | 'warning'; message: string }[] = []

  if (!flags.studentsCanCreateGroups) {
    results.push({
      type: 'error',
      message: "A permissão 'Estudantes criam grupos' é obrigatória neste modo.",
    })
  }
  if (!flags.studentsCanLeaveGroups && flags.groupLeaderCanDissolve) {
    results.push({
      type: 'error',
      message: 'Dissolução é uma saída forçada coletiva. Contradiz a restrição de saída voluntária.',
    })
  }
  if (flags.studentsCanCreateGroups && flags.supervisorCanEditGroups && !flags.studentsCanLeaveGroups) {
    results.push({
      type: 'warning',
      message:
        "Autoridade sobre composição compartilhada com saída bloqueada. Verifique se 'Estudantes podem sair' deve permanecer desativado.",
    })
  }
  return results
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

function getTodayString() {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

export function CreateAssignmentPage() {
  const navigate = useNavigate()
  const { id: courseId } = useParams<{ id: string }>()
  const { create, isLoading } = useCreateAssignment(courseId!)

  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [dueDate, setDueDate] = useState('')
  const [maxGroupMembers, setMaxGroupMembers] = useState(4)
  const [noLimit, setNoLimit] = useState(false)
  const [flags, setFlags] = useState<Omit<AssignmentFlags, 'maxGroupMembers' | 'maxGroups'>>(
    PRESETS.free
  )
  const [submitted, setSubmitted] = useState(false)
  const [showWarningModal, setShowWarningModal] = useState(false)

  const [forcedAdvanced, setForcedAdvanced] = useState(false)
  const mode = forcedAdvanced ? 'advanced' : detectMode(flags)
  const matchingPreset = detectMode(flags)
  const validations = getValidationErrors(flags, mode)
  
  if (dueDate) {
    const todayStr = getTodayString()
    if (dueDate < todayStr) {
      validations.push({
        type: 'error',
        message: 'A data de entrega não pode ser anterior à data atual.',
      })
    }
  }

  const hasErrors = validations.some((v) => v.type === 'error')

  function applyPreset(preset: Exclude<AssignmentMode, 'advanced'>) {
    setFlags(PRESETS[preset])
    setSubmitted(false)
  }

  function toggleFlag(key: keyof Omit<AssignmentFlags, 'maxGroupMembers' | 'maxGroups'>) {
    setFlags((prev) => ({ ...prev, [key]: !prev[key] }))
    setSubmitted(false)
  }

  async function handleSubmit() {
    setSubmitted(true)
    if (!name.trim() || hasErrors) return

    const hasWarnings = validations.some((v) => v.type === 'warning')
    if (hasWarnings && !showWarningModal) {
      setShowWarningModal(true)
      return
    }

    await proceedSubmit()
  }

  async function proceedSubmit() {
    setShowWarningModal(false)
    try {
      await create({
        name: name.trim(),
        description: description.trim() || 'Sem descrição',
        dueDate: dueDate ? new Date(dueDate + 'T23:59:59').toISOString() : new Date('2026-12-31T23:59:59').toISOString(),
        assignmentFlags: {
          ...flags,
          maxGroupMembers: noLimit ? 999 : maxGroupMembers,
          maxGroups: 999,
        },
      })
      navigate(-1)
    } catch (error) {
      // Error handled in mutation hook
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
        <h1 className={styles.title}>Criar trabalho</h1>
      </header>

      <div className={styles.content}>
        <div className={styles.field}>
          <label className={styles.label}>Nome do trabalho</label>
          <input
            className={styles.input}
            type="text"
            placeholder="Modelagem Relacional — Sistema de Biblioteca"
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
            placeholder="Descreva o trabalho..."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={4}
          />
        </div>

        <div className={styles.field}>
          <label className={styles.label}>
            Data de entrega <span className={styles.optional}>(opcional)</span>
          </label>
          <div className={styles.inputWrapper}>
            <input
              className={styles.input}
              type="date"
              value={dueDate}
              min={getTodayString()}
              onChange={(e) => setDueDate(e.target.value)}
            />
          </div>
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
            <span className={styles.counterValue}>{noLimit ? '∞' : maxGroupMembers}</span>
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
            {(['free', 'moderate', 'controlled', 'advanced'] as AssignmentMode[]).map((m) => {
              const isDisabled = m === 'moderate' || m === 'controlled'
              return (
                <button
                  key={m}
                  className={`${styles.modeCard} ${mode === m ? styles.modeCardActive : ''} ${
                    isDisabled ? styles.modeCardDisabled : ''
                  }`}
                  disabled={isDisabled}
                  onClick={() => {
                    if (m === 'advanced') {
                      setForcedAdvanced(true)
                      setFlags({
                        studentsCanCreateGroups: false,
                        studentsCanLeaveGroups: false,
                        groupLeaderCanDissolve: false,
                        groupLeaderCanRemoveMembers: false,
                        groupLeaderCanChangeMode: false,
                        groupLeaderCanTransferLeadership: false,
                        supervisorCanEditGroups: false,
                      })
                      setSubmitted(false)
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
                        ? 'Moderado (Desativado)'
                        : m === 'controlled'
                          ? 'Controlado (Desativado)'
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
              )
            })}
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
            {matchingPreset !== 'advanced' && (
              <div className={styles.alertInfo} style={{ marginTop: '12px' }}>
                <svg
                  width="18"
                  height="18"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2.5"
                  strokeLinecap="round"
                >
                  <circle cx="12" cy="12" r="10" />
                  <path d="M12 16v-4M12 8h.01" />
                </svg>
                <p>
                  Esta combinação de permissões corresponde ao preset{' '}
                  <strong>
                    {matchingPreset === 'free'
                      ? 'Livre'
                      : matchingPreset === 'moderate'
                        ? 'Moderado'
                        : 'Controlado'}
                  </strong>
                  .
                </p>
              </div>
            )}
          </div>
        )}

        {submitted && validations.length > 0 && (
          <div className={styles.alertStack}>
            {validations.map((v, i) => (
              <div
                key={i}
                className={`${styles.alert} ${v.type === 'error' ? styles.alertError : styles.alertWarning}`}
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
                  {v.type === 'error' ? (
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
                <p>{v.message}</p>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className={styles.footer}>
        <button
          className={styles.submitBtn}
          onClick={handleSubmit}
          disabled={!name.trim() || (submitted && hasErrors) || isLoading}
        >
          {isLoading ? 'Criando...' : 'Criar trabalho'}
        </button>
      </div>
      {showWarningModal && (
        <>
          <div className={styles.overlay} onClick={() => setShowWarningModal(false)} />
          <div className={styles.confirmModal}>
            <button className={styles.confirmModalCloseBtn} onClick={() => setShowWarningModal(false)}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M18 6L6 18M6 6l12 12" />
              </svg>
            </button>

            <p className={styles.confirmModalTitle}>Aviso de Configuração</p>
            <p className={styles.confirmModalSub}>
              Algumas configurações de permissões podem causar comportamentos indesejados. Tem certeza que deseja continuar?
            </p>

            <div className={styles.modalWarningsList}>
              {validations
                .filter((v) => v.type === 'warning')
                .map((w, index) => (
                  <div key={index} className={styles.modalWarningItem}>
                    <svg
                      width="16"
                      height="16"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2.5"
                    >
                      <circle cx="12" cy="12" r="10" />
                      <path d="M12 8v4M12 16h.01" />
                    </svg>
                    <span>{w.message}</span>
                  </div>
                ))}
            </div>

            <div className={styles.confirmModalActions}>
              <button className={styles.confirmModalCancelBtn} onClick={() => setShowWarningModal(false)}>
                Revisar Configurações
              </button>
              <button
                className={styles.confirmModalConfirmBtn}
                onClick={proceedSubmit}
                disabled={isLoading}
              >
                {isLoading ? 'Criando...' : 'Confirmar Criação'}
              </button>
            </div>
          </div>
        </>
      )}
    </main>
  )
}
