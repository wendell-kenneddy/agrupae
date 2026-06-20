import styles from './LoadingScreen.module.css'

export function LoadingScreen() {
  return (
    <div className={styles.loadingScreen}>
      <div className={styles.loadingContent}>
        <img
          src="/logo-icon.svg"
          alt="Carregando Agrupaê"
          className={styles.loadingLogo}
        />
        <p className={styles.loadingText}>Carregando...</p>
      </div>
    </div>
  )
}
