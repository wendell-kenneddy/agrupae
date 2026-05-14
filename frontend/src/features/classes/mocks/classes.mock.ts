import type { Class } from '@/features/classes/types/classes.types'

export const classesMock: Class[] = [
  {
    id: '1',
    name: 'Engenharia de Software - Turma A - 2026.1',
    memberCount: 36,
    activeAssignments: 4,
    role: 'OWNER',
    inviteCode: 'ES-2026-A1B2C',
    previewMembers: [
      { id: '1', name: 'Ana Beatriz Souza' },
      { id: '2', name: 'Fernanda Lima' },
      { id: '3', name: 'Carlos Eduardo Mendes' },
    ],
  },
  {
    id: '2',
    name: 'Banco de dados - 2026.1',
    memberCount: 31,
    activeAssignments: 2,
    role: 'OWNER',
    inviteCode: 'BD-2026-K7X9M',
    previewMembers: [
      { id: '4', name: 'Gabriel Oliveira' },
      { id: '5', name: 'João Pedro Alves' },
      { id: '6', name: 'Isabela Rocha' },
    ],
  },
  {
    id: '3',
    name: 'Interação Humano Computador - 2026.1',
    memberCount: 40,
    activeAssignments: 6,
    role: 'STUDENT',
    inviteCode: 'IHC-2026-X3Y4Z',
    previewMembers: [
      { id: '7', name: 'Larissa Ferreira' },
      { id: '8', name: 'Matheus Costa' },
      { id: '9', name: 'Rafael Nascimento' },
    ],
  },
  {
    id: '4',
    name: 'Teoria da Computação - 2026.1',
    memberCount: 22,
    activeAssignments: 1,
    role: 'STUDENT',
    inviteCode: 'TC-2026-M5N6O',
    previewMembers: [
      { id: '10', name: 'Victor Hugo Santos' },
      { id: '11', name: 'Thaís Monteiro' },
      { id: '12', name: 'Natália Barbosa' },
    ],
  },
]
