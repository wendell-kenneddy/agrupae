import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useGetClassMembers } from '@/features/classes/hooks/useGetClassMembers'
import { useTransferOwnership } from '@/features/classes/hooks/useTransferOwnership'
import { useAuth } from '@/app/providers/AuthContext'
import { useGetClass } from '@/features/classes/hooks/useGetClass'
import styles from './TransferOwnershipPage.module.css'
import type { Member } from '@/features/classes/types/classes.types'

import { UserAvatar } from '@/components/ui/UserAvatar'

function MemberAvatar({ name, large }: { name: string; large?: boolean }) {
  if (large) {
    return (
      <UserAvatar
        name={name}
        style={{ width: '64px', height: '64px', fontSize: '1.5rem', borderWidth: '2.5px', boxShadow: '2px 2px 0px #000' }}
      />
    )
  }
  return <UserAvatar name={name} size="md" />
}

export function TransferOwnershipPage() {
  const navigate = useNavigate()
  const { id } = useParams<{ id: string }>()
  const { user } = useAuth()
  const { members, isLoading, isError } = useGetClassMembers(id!)
  const { transfer, isLoading: isTransferring } = useTransferOwnership(id!)
  const { course } = useGetClass(id!)
  const [search, setSearch] = useState('')
  const [selected, setSelected] = useState<Member | null>(null)

  // exclui o próprio usuário da lista
  const filtered = members
    .filter((m) => m.id !== user?.id)
    .filter(
      (m) =>
        m.name.toLowerCase().includes(search.toLowerCase()) ||
        m.email.toLowerCase().includes(search.toLowerCase())
    )

  async function handleConfirm() {
    if (!selected) return
    await transfer(selected.id)
    setSelected(null)
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
        <h1 className={styles.title}>Transferir responsabilidade</h1>
      </header>

      <div className={styles.content}>
        <div className={styles.searchField}>
          <label className={styles.label}>Buscar usuário</label>
          <div className={styles.inputWrapper}>
            <input
              className={styles.input}
              type="text"
              placeholder="Nome ou email"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
            <svg
              className={styles.searchIcon}
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
            >
              <circle cx="11" cy="11" r="8" />
              <path d="M21 21l-4.35-4.35" strokeLinecap="round" />
            </svg>
          </div>
          <p className={styles.hint}>
            Somente usuários cadastrados no sistema podem receber a responsabilidade
          </p>
        </div>

        <div className={styles.list}>
          {isLoading && <p className={styles.feedback}>Carregando membros...</p>}
          {isError && <p className={styles.feedback}>Erro ao carregar membros.</p>}
          {filtered.map((m) => (
            <button key={m.id} className={styles.memberItem} onClick={() => setSelected(m)}>
              <MemberAvatar name={m.name} />
              <div className={styles.memberInfo}>
                <p className={styles.memberName}>{m.name}</p>
                <p className={styles.memberEmail}>{m.email}</p>
              </div>
            </button>
          ))}
        </div>
      </div>

      {selected && (
        <>
          <div className={styles.overlay} onClick={() => setSelected(null)} />
          <div className={styles.modal}>
            <button className={styles.closeBtn} onClick={() => setSelected(null)}>
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
              Transferir responsabilidade da turma <strong>{course?.name ?? '...'}</strong> para
            </p>
            <MemberAvatar name={selected.name} large />
            <p className={styles.modalName}>{selected.name}</p>
            <p className={styles.modalEmail}>{selected.email}</p>
            <p className={styles.modalWarning}>
              Após a transferência, você perderá todos os poderes de gestão desta turma.
            </p>
            <div className={styles.modalActions}>
              <button className={styles.cancelBtn} onClick={() => setSelected(null)}>
                Cancelar
              </button>
              <button
                className={styles.confirmBtn}
                onClick={handleConfirm}
                disabled={isTransferring}
              >
                {isTransferring ? 'Transferindo...' : 'Confirmar'}
              </button>
            </div>
          </div>
        </>
      )}
    </main>
  )
}
