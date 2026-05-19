import type { Class } from '@/features/classes/types/classes.types'
import styles from './ClassMembersTab.module.css'

interface ClassMembersTabProps {
  course: Class
}

const membersMock = [
  { id: '1', name: 'Ana Beatriz Souza', email: 'anabeatriz@universidade.br' },
  { id: '2', name: 'Fernanda Lima', email: 'fernandalima@universidade.br' },
  { id: '3', name: 'Carlos Eduardo Mendes', email: 'carlosmendes@universidade.br' },
  { id: '4', name: 'Gabriel Oliveira', email: 'gabrieloliveira@universidade.br' },
  { id: '5', name: 'João Pedro Alves', email: 'joaoalves@universidade.br' },
  { id: '6', name: 'Isabela Rocha', email: 'isabelarocha@universidade.br' },
  { id: '7', name: 'Larissa Ferreira', email: 'larissaferreira@universidade.br' },
  { id: '8', name: 'Matheus Costa', email: 'matheuscosta@universidade.br' },
]

function MemberAvatar({ name }: { name: string }) {
  const initials = name
    .split(' ')
    .slice(0, 2)
    .map((n) => n[0])
    .join('')

  return <div className={styles.avatar}>{initials}</div>
}

export function ClassMembersTab({ course }: ClassMembersTabProps) {
  return (
    <div className={styles.container}>
      {membersMock.map((m) => (
        <div key={m.id} className={styles.memberItem}>
          <MemberAvatar name={m.name} />
          <div className={styles.memberInfo}>
            <p className={styles.memberName}>{m.name}</p>
            <p className={styles.memberEmail}>{m.email}</p>
          </div>
        </div>
      ))}
    </div>
  )
}
