import { useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { useJoinClass } from '@/features/classes/hooks/useJoinClass.ts'
import styles from './JoinClassPage.module.css'

const joinClassSchema = z.object({
  inviteCode: z.string().min(1, 'Código de convite é obrigatório'),
})

type JoinClassFormData = z.infer<typeof joinClassSchema>

export function JoinClassPage() {
  const navigate = useNavigate()
  const { handleJoinClass, isLoading, error } = useJoinClass()
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<JoinClassFormData>({ resolver: zodResolver(joinClassSchema) })

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <button className={styles.backBtn} onClick={() => navigate(-1)}>
          <svg
            width="40"
            height="40"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="3"
          >
            <path d="M19 12H5M12 5l-7 7 7 7" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
        </button>
        <h1 className={styles.title}>Entrar em turma</h1>
      </header>

      <form className={styles.form} onSubmit={handleSubmit(handleJoinClass)}>
        <div className={styles.field}>
          <label className={styles.label}>Código de convite</label>
          <input
            className={`${styles.input} ${errors.inviteCode ? styles.error : ''}`}
            type="text"
            placeholder="Ex: BD-2026-K7X9M"
            {...register('inviteCode')}
          />
          {errors.inviteCode ? (
            <span className={styles.errorMsg}>{errors.inviteCode.message}</span>
          ) : error ? (
            <span className={styles.errorMsg}>{error}</span>
          ) : (
            <span className={styles.hint}>Peça o código ao responsável pela turma.</span>
          )}
        </div>

        <button className={styles.submitBtn} type="submit" disabled={isLoading}>
          {isLoading ? 'Entrando...' : 'Entrar na turma'}
        </button>

        {error && <span className={styles.errorMsg}>{error}</span>}
      </form>
    </main>
  )
}
