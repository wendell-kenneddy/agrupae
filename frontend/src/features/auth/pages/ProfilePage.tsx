import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '@/app/providers/AuthContext'
import { useLogout } from '@/features/auth/hooks/useLogout'
import { toast } from '@/components/ui/useToast'
import api from '@/lib/axios'
import styles from './ProfilePage.module.css'

export function ProfilePage() {
  const navigate = useNavigate()
  const { user, setUser } = useAuth()
  const { handleLogout } = useLogout()

  const [name, setName] = useState(user?.name ?? '')
  const [email, setEmail] = useState(user?.email ?? '')
  const [isLoading, setIsLoading] = useState(false)

  const initials = name
    .split(' ')
    .slice(0, 2)
    .map((n) => n[0])
    .join('')
    .toUpperCase()

  const hasChanges = name.trim() !== (user?.name ?? '') || email.trim() !== (user?.email ?? '')

  async function handleSave() {
    if (!name.trim() || !email.trim()) return
    if (!hasChanges) return
    setIsLoading(true)
    try {
      const response = await api.put('/users/me', { name: name.trim(), email: email.trim() })
      setUser({ id: response.data.id, name: response.data.name, email: response.data.email })
      toast.success('Alterações salvas com sucesso!')
    } finally {
      setIsLoading(false)
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
        <h1 className={styles.title}>Perfil</h1>
      </header>

      <div className={styles.content}>
        <div className={styles.avatarSection}>
          <div className={styles.avatar}>{initials}</div>
        </div>

        <div className={styles.fields}>
          <div className={styles.field}>
            <label className={styles.label}>Nome</label>
            <input
              className={styles.input}
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>

          <div className={styles.field}>
            <label className={styles.label}>Email</label>
            <input
              className={styles.input}
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>
        </div>

        <button
          className={styles.saveBtn}
          onClick={handleSave}
          disabled={!name.trim() || !email.trim() || isLoading || !hasChanges}
        >
          {isLoading ? 'Salvando...' : 'Salvar alterações'}
        </button>


        <button className={styles.logoutBtn} onClick={handleLogout}>
          <svg
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2.5"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
            <polyline points="16 17 21 12 16 7" />
            <line x1="21" y1="12" x2="9" y2="12" />
          </svg>
          Sair da conta
        </button>
      </div>
    </main>
  )
}
