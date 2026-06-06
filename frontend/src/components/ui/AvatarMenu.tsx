import { useNavigate } from 'react-router-dom'
import styles from './AvatarMenu.module.css'

export function AvatarMenu() {
  const navigate = useNavigate()

  return <button className={styles.avatarBtn} onClick={() => navigate('/profile')} />
}
