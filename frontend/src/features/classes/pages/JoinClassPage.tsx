import { useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import styles from './JoinClassPage.module.css'

const joinClassSchema = z.object({
  code: z.string().min(1, 'Código de convite é obrigatório'),
})

type JoinClassFormData = z.infer<typeof joinClassSchema>

export function JoinClassPage() {
  const navigate = useNavigate()
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<JoinClassFormData>({ resolver: zodResolver(joinClassSchema) })

  function onSubmit(data: JoinClassFormData) {
    console.log(data)
    // integrar com API depois
  }

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

      <form className={styles.form} onSubmit={handleSubmit(onSubmit)}>
        <div className={styles.field}>
          <label className={styles.label}>Código de convite</label>
          <input
            className={`${styles.input} ${errors.code ? styles.error : ''}`}
            type="text"
            placeholder="Ex: BD-2026-K7X9M"
            {...register('code')}
          />
          {errors.code ? (
            <span className={styles.errorMsg}>{errors.code.message}</span>
          ) : (
            <span className={styles.hint}>Peça o código ao responsável pela turma.</span>
          )}
        </div>

        <button className={styles.submitBtn} type="submit">
          Entrar na turma
        </button>
      </form>
    </main>
  )
}
