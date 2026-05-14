import { useNavigate } from 'react-router-dom'
import { RegisterForm } from '@/features/auth/components/RegisterForm'
import styles from './RegisterPage.module.css'
import { FaArrowLeft } from 'react-icons/fa'

export function RegisterPage() {
  const navigate = useNavigate()

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <button className={styles.backBtn} onClick={() => navigate(-1)}>
          <FaArrowLeft className={styles.backBtn} />
        </button>
        <img src="/logo-completa.svg" alt="Agrupaê" className={styles.logo} />
      </header>
      <div className={styles.body}>
        <h1 className={styles.title}>Cadastraê</h1>
        <RegisterForm />
      </div>
    </main>
  )
}
