import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useGetCourses } from '@/features/classes/hooks/useGetCourses'
import { ClassCard } from '@/features/classes/components/ClassCard'
import { AvatarMenu } from '@/components/ui/AvatarMenu'
import { TiPlus } from 'react-icons/ti'
import styles from './HomeClassesPage.module.css'

function ChevronIcon() {
  return (
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
      <path d="M6 9l6 6 6-6" />
    </svg>
  )
}

export function HomeClassesPage() {
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)
  const [showArchived, setShowArchived] = useState(false)
  const { courses, isLoading, isError } = useGetCourses()
  const myClasses = courses.filter((c) => c.role === 'OWNER' && !c.archived)
  const joinedClasses = courses.filter((c) => c.role === 'STUDENT' && !c.archived)
  const archivedClasses = courses.filter((c) => c.archived)

  if (isLoading) return <div className={styles.feedback}>Carregando turmas...</div>
  if (isError) return <div className={styles.feedback}>Erro ao carregar turmas.</div>

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <img src="/logo-completa.svg" alt="Agrupaê" className={styles.logo} />
        <AvatarMenu />
      </header>

      <div className={styles.content}>
        {courses.length === 0 ? (
          <div className={styles.emptyState}>
            <div className={styles.emptyStateIcon}>
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
                <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" />
              </svg>
            </div>
            <h3 className={styles.emptyStateTitle}>Nenhuma turma por aqui</h3>
            <p className={styles.emptyStateText}>
              Crie uma turma para gerenciar seus trabalhos ou entre em uma com o código fornecido pelo professor.
            </p>
            <div className={styles.emptyStateActions}>
              <button
                className={styles.ctaButton}
                onClick={() => navigate('/classes/create')}
              >
                Criar turma
              </button>
              <button
                className={`${styles.ctaButton} ${styles.ctaButtonSecondary}`}
                onClick={() => navigate('/classes/join')}
              >
                Entrar com código
              </button>
            </div>
          </div>
        ) : (
          <>
            <section className={styles.section}>
              <h2 className={styles.sectionTitle}>Minhas turmas</h2>
              {myClasses.length > 0 ? (
                myClasses.map((c) => (
                  <ClassCard key={c.id} class={c} />
                ))
              ) : (
                <div className={styles.sectionEmptyState}>
                  <span>Você ainda não criou nenhuma turma.</span>
                  <button
                    className={styles.inlineLink}
                    onClick={() => navigate('/classes/create')}
                  >
                    Criar turma
                  </button>
                </div>
              )}
            </section>

            <hr className={styles.divider} />

            <section className={styles.section}>
              <h2 className={styles.sectionTitle}>Turmas que participo</h2>
              {joinedClasses.length > 0 ? (
                joinedClasses.map((c) => (
                  <ClassCard key={c.id} class={c} />
                ))
              ) : (
                <div className={styles.sectionEmptyState}>
                  <span>Você ainda não entrou em nenhuma turma.</span>
                  <button
                    className={styles.inlineLink}
                    onClick={() => navigate('/classes/join')}
                  >
                    Entrar com código
                  </button>
                </div>
              )}
            </section>

            {archivedClasses.length > 0 && (
              <>
                <hr className={styles.divider} />
                <section className={styles.section}>
                  <button
                    className={styles.archivedHeader}
                    onClick={() => setShowArchived(!showArchived)}
                  >
                    <span>Turmas arquivadas ({archivedClasses.length})</span>
                    <span className={`${styles.chevron} ${showArchived ? styles.chevronExpanded : ''}`}>
                      <ChevronIcon />
                    </span>
                  </button>

                  {showArchived && (
                    <div className={styles.archivedList}>
                      {archivedClasses.map((c) => (
                        <ClassCard key={c.id} class={c} />
                      ))}
                    </div>
                  )}
                </section>
              </>
            )}
          </>
        )}
      </div>

      {menuOpen && (
        <>
          <div className={styles.overlay} onClick={() => setMenuOpen(false)} />
          <div className={styles.menu}>
            <button className={styles.menuItem} onClick={() => navigate('/classes/create')}>
              <TiPlus />
              Criar turma
            </button>
            <button className={styles.menuItem} onClick={() => navigate('/classes/join')}>
              <svg
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2.5"
              >
                <path
                  d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4M10 17l5-5-5-5M15 12H3"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                />
              </svg>
              Entrar com código
            </button>
          </div>
        </>
      )}

      <button className={styles.fab} onClick={() => setMenuOpen(!menuOpen)}>
        +
      </button>
    </main>
  )
}
