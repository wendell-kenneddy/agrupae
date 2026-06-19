import { useNavigate } from 'react-router-dom'
import { useAuth } from '@/app/providers/AuthContext'
import styles from './AvatarMenu.module.css'

export function AvatarMenu() {
  const navigate = useNavigate()
  const { user } = useAuth()
  const avatarUrl = `https://api.dicebear.com/7.x/avataaars/svg?seed=${encodeURIComponent(user?.id || 'default')}`

  return (
    <button className={styles.avatarBtn} onClick={() => navigate('/profile')}>
      <img src={avatarUrl} alt="Profile Avatar" className={styles.avatarImg} />
    </button>
  )
}

