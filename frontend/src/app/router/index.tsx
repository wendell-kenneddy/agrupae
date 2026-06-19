import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom'
import { useAuth } from '@/app/providers/AuthContext'
import { getAccessToken } from '@/lib/axios'
import { LandingPage } from '@/features/auth/pages/LandingPage'
import { LoginPage } from '@/features/auth/pages/LoginPage'
import { ProfilePage } from '@/features/auth/pages/ProfilePage'
import { RegisterPage } from '@/features/auth/pages/RegisterPage'
import { HomeClassesPage } from '@/features/classes/pages/HomeClassesPage'
import { CreateClassPage } from '@/features/classes/pages/CreateClassPage'
import { JoinClassPage } from '@/features/classes/pages/JoinClassPage'
import { ClassPage } from '@/features/classes/pages/ClassPage'
import { TransferOwnershipPage } from '@/features/classes/pages/TransferOwnershipPage'
import { CreateAssignmentPage } from '@/features/assignments/pages/CreateAssignmentPage'
import { AssignmentPage } from '@/features/assignments/pages/AssignmentPage'
import { EditAssignmentPage } from '@/features/assignments/pages/EditAssignmentPage'
import { GroupPage } from '@/features/group/pages/GroupPage'
import { NotFoundPage } from '@/components/ui/NotFoundPage'

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
  {
    path: '/classes/create',
    element: (
      <ProtectedRoute>
        <CreateClassPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/classes/join',
    element: (
      <ProtectedRoute>
        <JoinClassPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/home',
    element: (
      <ProtectedRoute>
        <HomeClassesPage />
      </ProtectedRoute>
    ),
  },

  {
    path: '/profile',
    element: (
      <ProtectedRoute>
        <ProfilePage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/classes/:id',
    element: (
      <ProtectedRoute>
        <ClassPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/classes/:id/transfer',
    element: (
      <ProtectedRoute>
        <TransferOwnershipPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/classes/:id/assignments/create',
    element: (
      <ProtectedRoute>
        <CreateAssignmentPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/classes/:id/assignments/:assignmentId',
    element: (
      <ProtectedRoute>
        <AssignmentPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/classes/:id/assignments/:assignmentId/edit',
    element: (
      <ProtectedRoute>
        <EditAssignmentPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/classes/:id/assignments/:assignmentId/groups/:groupId',
    element: (
      <ProtectedRoute>
        <GroupPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '*',
    element: <NotFoundPage />,
  },
])

export function Router() {
  return <RouterProvider router={router} />
}
