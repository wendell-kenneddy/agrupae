import { useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import styles from './CreateClassPage.module.css'

const createClassSchema = z.object({
  name: z.string().min(1, 'Nome da turma é obrigatório'),
})

type CreateClassFormData = z.infer<typeof createClassSchema>

export function CreateClassPage() {
  const navigate = useNavigate()
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<CreateClassFormData>({ resolver: zodResolver(createClassSchema) })

  const nameValue = watch('name')

  function onSubmit(data: CreateClassFormData) {
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
        <h1 className={styles.title}>Criar turma</h1>
      </header>

      <form className={styles.form} onSubmit={handleSubmit(onSubmit)}>
        <div className={styles.field}>
          <label className={styles.label}>Nome da turma</label>
          <input
            className={`${styles.input} ${errors.name ? styles.error : ''}`}
            type="text"
            placeholder="Ex: Engenharia de Software - Turma A - 2026.1"
            {...register('name')}
          />
          {errors.name ? (
            <span className={styles.errorMsg}>{errors.name.message}</span>
          ) : (
            <span className={styles.hint}>
              Este nome será visível para todos os membros da turma.
            </span>
          )}
        </div>

        <button className={styles.submitBtn} type="submit">
          Criar turma
        </button>
      </form>
    </main>
  )
}
