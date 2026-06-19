import { useNavigate } from 'react-router-dom'
import { useAuth } from '@/app/providers/AuthContext'
import { UserAvatar } from './UserAvatar'

export function AvatarMenu() {
  const navigate = useNavigate()
  const { user } = useAuth()

  return (
    <button 
      onClick={() => navigate('/profile')} 
      style={{ padding: 0, border: 'none', background: 'none', cursor: 'pointer', display: 'flex' }}
    >
      <UserAvatar name={user?.name || 'User'} size="lg" />
    </button>
  )
}

