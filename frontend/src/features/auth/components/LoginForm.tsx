import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'

import { useLogin } from '@/features/auth/hooks/useLogin'
import { useForm } from 'react-hook-form'
import { useState } from 'react'
import { FaEyeSlash, FaRegEyeSlash } from 'react-icons/fa'

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
    <form onSubmit={handleSubmit(handleLogin)}>
      <div>
        <label>Email</label>
        <input type="email" {...register('email')} />
        {errors.email && <span>{errors.email.message}</span>}
      </div>
      <div>
        <div>
          <label>Senha</label>
          <input type={showPassword ? 'text' : 'password'} {...register('password')} />
          <button type="button" onClick={() => setShowPassword(!showPassword)}>
            {showPassword ? <FaEyeSlash /> : <FaRegEyeSlash />}
          </button>
          {errors.password && <span>{errors.password.message}</span>}
        </div>
        <p>Esqueceu sua senha?</p>
      </div>

      {error && <span>{error}</span>}

      <button type="submit" disabled={isLoading}>
        {isLoading ? 'Carregando...' : 'Entrar'}
      </button>
      <p>
        Ainda não possui um cadastro? <a>Cadastrar</a>
      </p>
    </form>
  )
}
