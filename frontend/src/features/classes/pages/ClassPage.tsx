import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { classesMock } from '@/features/classes/mocks/classes.mock'
import { ClassAssignmentsTab } from '@/features/classes/components/ClassAssignmentsTab.tsx'
import { ClassMembersTab } from '@/features/classes/components/ClassMembersTab'
import { ClassInfoTab } from '@/features/classes/components/ClassInfoTab'
import { AvatarMenu } from '@/components/ui/AvatarMenu'
import styles from './ClassPage.module.css'

type Tab = 'assignments' | 'members' | 'info'

export function ClassPage() {
  const navigate = useNavigate()
  const { id } = useParams<{ id: string }>()
  const [activeTab, setActiveTab] = useState<Tab>('assignments')

  const course = classesMock.find((c) => c.id === id)

  if (!course) return <div className={styles.feedback}>Turma não encontrada.</div>

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
        <span className={styles.headerTitle}>{course.name}</span>
        <AvatarMenu />
      </header>

      <div className={styles.content}>
        {activeTab === 'assignments' && <ClassAssignmentsTab course={course} />}
        {activeTab === 'members' && <ClassMembersTab course={course} />}
        {activeTab === 'info' && <ClassInfoTab course={course} />}
      </div>

      <nav className={styles.bottomNav}>
        <button
          className={`${styles.navItem} ${activeTab === 'assignments' ? styles.active : ''}`}
          onClick={() => setActiveTab('assignments')}
        >
          <svg
            width="22"
            height="22"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2.5"
          >
            <rect x="8" y="2" width="8" height="4" rx="1" />
            <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2" />
          </svg>
          <span>Trabalhos</span>
        </button>

        <button
          className={`${styles.navItem} ${activeTab === 'members' ? styles.active : ''}`}
          onClick={() => setActiveTab('members')}
        >
          <svg
            width="22"
            height="22"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2.5"
          >
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
            <circle cx="9" cy="7" r="4" />
            <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" />
          </svg>
          <span>Membros</span>
        </button>

        <button
          className={`${styles.navItem} ${activeTab === 'info' ? styles.active : ''}`}
          onClick={() => setActiveTab('info')}
        >
          <svg
            width="22"
            height="22"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2.5"
          >
            <circle cx="12" cy="12" r="10" />
            <path d="M12 16v-4M12 8h.01" strokeLinecap="round" />
          </svg>
          <span>Informações</span>
        </button>
      </nav>
    </main>
  )
}
