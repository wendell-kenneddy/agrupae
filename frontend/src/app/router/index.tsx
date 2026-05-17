import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom'
import { useAuth } from '@/app/providers/AuthContext'
import { getAccessToken } from '@/lib/axios'
import { LandingPage } from '@/features/auth/pages/LandingPage'
import { LoginPage } from '@/features/auth/pages/LoginPage'
import { RegisterPage } from '@/features/auth/pages/RegisterPage'
import { HomeClassesPage } from '@/features/classes/pages/HomeClassesPage'
import { CreateClassPage } from '@/features/classes/pages/CreateClassPage'
import { JoinClassPage } from '@/features/classes/pages/JoinClassPage'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isLoading } = useAuth()
  const token = getAccessToken()

  if (isLoading) return null

  if (!token) {
    return <Navigate to="/login" replace />
  }

  return <>{children}</>
}

const router = createBrowserRouter([
  { path: '/', element: <LandingPage /> },
  { path: '/login', element: <LoginPage /> },
  { path: '/register', element: <RegisterPage /> },
  { path: '/home', element: <HomeClassesPage /> },
  { path: '/classes/create', element: <CreateClassPage /> },
  { path: '/classes/join', element: <JoinClassPage /> },

  // {
  //   path: '/home',
  //   element: (
  //     <ProtectedRoute>
  //       <HomeClassesPage />
  //     </ProtectedRoute>
  //   ),
  // },
])

export function Router() {
  return <RouterProvider router={router} />
}
