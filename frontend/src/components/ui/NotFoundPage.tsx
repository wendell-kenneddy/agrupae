import { useNavigate } from 'react-router-dom'
import { useAuth } from '@/app/providers/AuthContext'
import styles from './NotFoundPage.module.css'

export function NotFoundPage() {
  const navigate = useNavigate()
  const { user } = useAuth()

  const handleRedirect = () => {
    if (user) {
      navigate('/home')
    } else {
      navigate('/')
    }
  }

  return (
    <main className={styles.notFoundPage}>
      <div className={styles.notFoundContent}>
        <div className={styles.notFoundIcon}>
          <svg
            width="48"
            height="48"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="1.5"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" />
            <line x1="12" y1="9" x2="12" y2="13" />
            <line x1="12" y1="17" x2="12.01" y2="17" />
          </svg>
        </div>
        <h1 className={styles.notFoundTitle}>Página não encontrada</h1>
        <p className={styles.notFoundDesc}>
          A página que você está procurando não existe ou você não tem permissão para acessá-la.
        </p>
        <button className={styles.notFoundBtn} onClick={handleRedirect}>
          Voltar para o início
        </button>
      </div>
    </main>
  )
}
