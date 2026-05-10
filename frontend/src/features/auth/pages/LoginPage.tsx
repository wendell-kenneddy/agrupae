import { useNavigate } from 'react-router-dom'
import { LoginForm } from '@/features/auth/components/LoginForm'

export function LoginPage() {
  const navigate = useNavigate()

  return (
    <main>
      <header>
        <button onClick={() => navigate(-1)}>←</button>
        <img src="/logo-completa.svg" alt="Agrupaê" />
      </header>
      <h1>Entraê</h1>
      <LoginForm />
    </main>
  )
}
