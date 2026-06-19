import { HTMLAttributes } from 'react'
import styles from './UserAvatar.module.css'

interface UserAvatarProps extends HTMLAttributes<HTMLDivElement> {
  name: string
  size?: 'sm' | 'md' | 'lg' | 'xl'
}

const AVATAR_COLORS = [
  { bg: 'var(--color-blue-light)', border: 'var(--color-blue-dark)', text: 'var(--color-blue-dark)' },
  { bg: 'var(--color-green-light)', border: 'var(--color-green)', text: 'var(--color-green)' },
  { bg: 'var(--color-red-light)', border: 'var(--color-red)', text: 'var(--color-red)' },
  { bg: 'var(--color-orange-light)', border: 'var(--color-orange)', text: 'var(--color-orange)' },
  { bg: 'var(--color-bg-gray)', border: 'var(--color-text-dark)', text: 'var(--color-text-dark)' },
]

export function getInitials(name: string): string {
  if (!name) return ''
  const words = name.trim().split(/\s+/).filter(w => w.length > 0)
  if (words.length === 0) return ''
  if (words.length === 1) return words[0].charAt(0).toUpperCase()
  
  const prepositions = ['da', 'de', 'do', 'dos', 'das']
  const filteredWords = words.filter((w, index) => index === 0 || !prepositions.includes(w.toLowerCase()))
  
  if (filteredWords.length >= 2) {
    return (filteredWords[0].charAt(0) + filteredWords[1].charAt(0)).toUpperCase()
  }
  
  // Fallback
  return (words[0].charAt(0) + (words[1] ? words[1].charAt(0) : '')).toUpperCase()
}

function getAvatarColor(name: string) {
  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }
  const index = Math.abs(hash) % AVATAR_COLORS.length
  return AVATAR_COLORS[index]
}

export function UserAvatar({ name, size = 'md', className = '', style, ...props }: UserAvatarProps) {
  const initials = getInitials(name)
  const colors = getAvatarColor(name)
  
  const sizeClass = styles[size] || styles.md
  
  const customStyle = {
    backgroundColor: colors.bg,
    borderColor: colors.border,
    color: colors.text,
    ...style,
  }

  return (
    <div
      className={`${styles.avatar} ${sizeClass} ${className}`}
      style={customStyle}
      {...props}
    >
      {initials}
    </div>
  )
}
