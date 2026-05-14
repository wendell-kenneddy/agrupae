import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { classesMock } from '@/features/classes/mocks/classes.mock'
import type { Class } from '@/features/classes/types/classes.types'
import { ClassCard } from '@/features/classes/components/ClassCard'

export function HomeClassesPage() {
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)

  const myClasses = classesMock.filter((c) => c.role === 'OWNER')
  const joinedClasses = classesMock.filter((c) => c.role === 'STUDENT')

  return (
    <main>
      <header>
        <img src="/logo-completa.svg" alt="Agrupaê" />
        <button onClick={() => navigate('/profile')}>
          <img src="/avatar-placeholder.png" alt="Perfil" />
        </button>
      </header>

      <section>
        <h2>Minhas turmas</h2>
        {myClasses.map((c) => (
          <ClassCard key={c.id} class={c} />
        ))}
      </section>

      <section>
        <h2>Turmas que participo</h2>
        {joinedClasses.map((c) => (
          <ClassCard key={c.id} class={c} />
        ))}
      </section>

      {menuOpen && (
        <div>
          <button onClick={() => navigate('/classes/create')}>+ Criar turma</button>
          <button onClick={() => navigate('/classes/join')}>→ Entrar com código</button>
        </div>
      )}

      <button onClick={() => setMenuOpen(!menuOpen)}>+</button>
    </main>
  )
}
