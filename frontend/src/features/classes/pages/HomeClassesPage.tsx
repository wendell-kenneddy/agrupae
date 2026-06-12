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
        <section className={styles.section}>
          <h2 className={styles.sectionTitle}>Minhas turmas</h2>
          {myClasses.map((c) => (
            <ClassCard key={c.id} class={c} />
          ))}
        </section>

        <hr className={styles.divider} />

        <section className={styles.section}>
          <h2 className={styles.sectionTitle}>Turmas que participo</h2>
          {joinedClasses.map((c) => (
            <ClassCard key={c.id} class={c} />
          ))}
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
