import { useNavigate } from 'react-router-dom'
import { RegisterForm } from '@/features/auth/components/RegisterForm'

export function RegisterPage() {
  const navigate = useNavigate()

  return (
    <main>
      <header>
        <button onClick={() => navigate(-1)}>←</button>
        <img src="/logo-completa.svg" alt="Agrupaê" />
      </header>
      <h1>Cadastraê</h1>
      <RegisterForm />
    </main>
  )
}
