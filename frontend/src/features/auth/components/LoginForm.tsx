import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'

import { useLogin } from '@/features/auth/hooks/useLogin'
import { useForm } from 'react-hook-form'
import { useState } from 'react'
import { Link } from 'react-router-dom'
import { FaEyeSlash, FaRegEyeSlash, FaRegEnvelope } from 'react-icons/fa'
import { MdOutlineLock } from 'react-icons/md'

import styles from './LoginForm.module.css'

const loginSchema = z.object({
  email: z.string().email('Email inválido'),
  password: z.string().min(8, 'Mínimo de 8 caracteres'),
})

type LoginFormData = z.infer<typeof loginSchema>

export function LoginForm() {
  const { handleLogin, isLoading, error } = useLogin()
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({ resolver: zodResolver(loginSchema) })
  const [showPassword, setShowPassword] = useState(false)

  return (
    <form className={styles.form} onSubmit={handleSubmit(handleLogin)}>
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
        {/* <p className={styles.forgotLink}>Esqueceu sua senha?</p> */}
      </div>

      {error && <span className={styles.globalError}>{error}</span>}

      <button className={styles.submitBtn} type="submit" disabled={isLoading}>
        {isLoading ? 'Carregando...' : 'Entrar'}
      </button>
      <p className={styles.footer}>
        Ainda não possui um cadastro?{' '}
        <Link className={styles.footerLink} to="/register">
          Cadastrar
        </Link>
      </p>
    </form>
  )
}
