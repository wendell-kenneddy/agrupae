import { useNavigate } from 'react-router-dom'

import styles from './LandingPage.module.css'

export function LandingPage() {
  const navigate = useNavigate()

  return (
    <main className={styles.page}>
      <div className={styles.content}>
        <img src="logo-completa.svg" alt="Agrupaê" className={styles.logo}></img>

        <h1 className={styles.title}>
          Onde o trabalho <br /> em grupo <span>finalmente funciona.</span>
        </h1>

        <p className={styles.subtitle}>
          Gerencie pessoas, arquivos e prazos com a organização que o seu semestre exige.
        </p>
      </div>

      <div className={styles.actions}>
        <button className={styles.btnPrimary} onClick={() => navigate('/register')}>
          Fazer cadastro
        </button>

        <button className={styles.btnSecondary} onClick={() => navigate('/login')}>
          Entrar
        </button>
      </div>
    </main>
  )
}
