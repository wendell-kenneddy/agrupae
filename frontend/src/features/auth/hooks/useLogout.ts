import { useNavigate } from 'react-router-dom'
import { logout } from '@/features/auth/api/authApi'
import { setAccessToken } from '@/lib/axios'
import { useAuth } from '@/app/providers/AuthContext'

export function useLogout() {
  const navigate = useNavigate()
  const { setUser } = useAuth()

  async function handleLogout() {
    try {
      await logout()
    } finally {
      setAccessToken(null)
      setUser(null)
      navigate('/login')
    }
  }

  return { handleLogout }
}
