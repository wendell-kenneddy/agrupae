import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useTransferOwnership } from '@/features/classes/hooks/useTransferOwnership'
import styles from './TransferOwnershipPage.module.css'

const membersMock = [
  { id: '1', name: 'Ana Beatriz Souza', email: 'anabeatriz@universidade.br' },
  { id: '2', name: 'Fernanda Lima', email: 'fernandalima@universidade.br' },
  { id: '3', name: 'Carlos Eduardo Mendes', email: 'carlosmendes@universidade.br' },
  { id: '4', name: 'Gabriel Oliveira', email: 'gabrieloliveira@universidade.br' },
  { id: '5', name: 'João Pedro Alves', email: 'joaoalves@universidade.br' },
  { id: '6', name: 'Isabela Rocha', email: 'isabelarocha@universidade.br' },
  { id: '7', name: 'Larissa Ferreira', email: 'larissaferreira@universidade.br' },
  { id: '8', name: 'Matheus Costa', email: 'matheuscosta@universidade.br' },
]

interface Member {
  id: string
  name: string
  email: string
}

function MemberAvatar({ name, large }: { name: string; large?: boolean }) {
  const initials = name
    .split(' ')
    .slice(0, 2)
    .map((n) => n[0])
    .join('')

  return <div className={large ? styles.avatarLarge : styles.avatar}>{initials}</div>
}

export function TransferOwnershipPage() {
  const navigate = useNavigate()
  const { id } = useParams<{ id: string }>()
  const { transfer, isLoading } = useTransferOwnership(id!)
  const [search, setSearch] = useState('')
  const [selected, setSelected] = useState<Member | null>(null)

  const filtered = membersMock.filter(
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
              Transferir responsabilidade da turma <strong>Banco de dados - 2026.1</strong> para
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
              <button className={styles.confirmBtn} onClick={handleConfirm} disabled={isLoading}>
                {isLoading ? 'Transferindo...' : 'Confirmar'}
              </button>
            </div>
          </div>
        </>
      )}
    </main>
  )
}
