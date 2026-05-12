import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'

import { useRegister } from '@/features/auth/hooks/useRegister'
import { useForm } from 'react-hook-form'
import { useState } from 'react'
import { Link } from 'react-router-dom'
import { FaEyeSlash, FaRegEyeSlash, FaRegUser, FaRegEnvelope } from 'react-icons/fa'
import { MdOutlineLock } from 'react-icons/md'

import styles from './RegisterForm.module.css'

const registerSchema = z.object({
  name: z.string(),
  email: z.string().email('Email inválido'),
  password: z.string().min(8, 'Mínimo de 8 caracteres'),
})

type registerFormData = z.infer<typeof registerSchema>

export function RegisterForm() {
  const { handleRegister, isLoading, error } = useRegister()
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<registerFormData>({ resolver: zodResolver(registerSchema) })
  const [showPassword, setShowPassword] = useState(false)

  return (
    <form className={styles.form} onSubmit={handleSubmit(handleRegister)}>
      <div className={styles.field}>
        <label className={styles.label}>Nome completo</label>
        <div className={styles.inputWrapper}>
          <FaRegUser className={styles.inputIcon} />
          <input
            className={`${styles.input} ${errors.name ? styles.error : ''}`}
            type="text"
            placeholder="Seu nome"
            {...register('name')}
          />
        </div>
        {errors.name && <span className={styles.errorMsg}>{errors.name.message}</span>}
      </div>

      <div className={styles.field}>
        <label className={styles.label}>Email</label>
        <div className={styles.inputWrapper}>
          <FaRegEnvelope className={styles.inputIcon} />
          <input
            className={`${styles.input} ${errors.email ? styles.error : ''}`}
            type="email"
            placeholder="seu@email.com"
            {...register('email')}
          />
        </div>
        {errors.email && <span className={styles.errorMsg}>{errors.email.message}</span>}
      </div>

      <div className={styles.field}>
        <label className={styles.label}>Senha</label>
        <div className={styles.inputWrapper}>
          <MdOutlineLock
            className={styles.inputIcon}
            style={{ width: '22px', height: '22px', left: 10 }}
          />
          <input
            className={`${styles.input} ${errors.password ? styles.error : ''}`}
            type={showPassword ? 'text' : 'password'}
            placeholder="••••••••"
            {...register('password')}
          />
          <button
            className={styles.toggleBtn}
            type="button"
            onClick={() => setShowPassword(!showPassword)}
          >
            {showPassword ? <FaEyeSlash /> : <FaRegEyeSlash />}
          </button>
        </div>
        {errors.password && <span className={styles.errorMsg}>{errors.password.message}</span>}
      </div>

      {error && <span className={styles.globalError}>{error}</span>}

      <button className={styles.submitBtn} type="submit" disabled={isLoading}>
        {isLoading ? 'Carregando...' : 'Cadastrar'}
      </button>
      <p className={styles.footer}>
        Já possui um cadastro?{' '}
        <Link className={styles.footerLink} to="/login">
          Entrar
        </Link>
      </p>
    </form>
  )
}
