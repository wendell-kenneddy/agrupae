import { useNavigate, Link } from 'react-router-dom'
import { LoginForm } from '@/features/auth/components/LoginForm'
import styles from './LoginPage.module.css'
import { FaArrowLeft } from 'react-icons/fa'

export function LoginPage() {
  const navigate = useNavigate()

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <button onClick={() => navigate('/')}>
          <FaArrowLeft className={styles.backBtn} />
        </button>
        <Link to="/">
          <img src="/logo-completa.svg" alt="Agrupaê" className={styles.logo} />
        </Link>
      </header>

      <div className={styles.body}>
        <h1 className={styles.title}>Entraê</h1>
        <LoginForm />
      </div>
    </main>
  )
}
