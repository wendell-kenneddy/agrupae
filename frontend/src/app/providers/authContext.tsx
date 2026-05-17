import { createContext, useContext, useState, useEffect } from 'react'
import type { ReactNode } from 'react'
import type { User } from '@/features/auth/types/auth.types'
import api, { setAccessToken } from '@/lib/axios'

import { getMe } from '@/features/auth/api/authApi'

interface AuthContextType {
  user: User | null
  setUser: (user: User | null) => void
  isLoading: boolean
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    async function restoreSession() {
      /*try {
        const response = await api.post<string>('/auth/refresh')
        setAccessToken(response.data)
        const user = await getMe()
        setUser(user)
      } catch {
        setAccessToken(null)
      } finally {
        setIsLoading(false)
      }
      */
      setIsLoading(false)
    }

    restoreSession()
  }, [])

  return (
    <AuthContext.Provider value={{ user, setUser, isLoading }}>{children}</AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)

  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }

  return context
}
