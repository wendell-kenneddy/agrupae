import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'

import { useRegister } from '@/features/auth/hooks/useRegister'
import { useForm } from 'react-hook-form'
import { useState } from 'react'
import { Link } from 'react-router-dom'
import { FaEyeSlash, FaRegEyeSlash } from 'react-icons/fa'

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
    <form onSubmit={handleSubmit(handleRegister)}>
      <div>
        <label>Nome completo</label>
        <input type="text" {...register('name')} />
        {errors.name && <span>{errors.name.message}</span>}
      </div>
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
      </div>

      {error && <span>{error}</span>}

      <button type="submit" disabled={isLoading}>
        {isLoading ? 'Carregando...' : 'Cadastrar'}
      </button>
      <p>
        Já possui um cadastro? <Link to="/login">Entrar</Link>
      </p>
    </form>
  )
}
