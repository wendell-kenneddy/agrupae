import type { Assignment, AssignmentArtifact } from '@/features/assignments/types/assignments.types'

export const assignmentsMock: Assignment[] = [
  {
    id: '1',
    courseId: '1',
    name: 'Modelagem Relacional - Sistema de Biblioteca',
    description:
      'Neste trabalho, cada grupo deverá desenvolver um conjunto de consultas SQL avançadas sobre um banco de dados relacional fornecido pelo professor. As consultas devem contemplar junções, subconsultas, funções de agregação e otimização de performance. O resultado final deve ser entregue em arquivo .sql acompanhado de um relatório explicando as decisões tomadas.',
    assignmentFlags: {
      maxGroupMembers: 5,
      maxGroups: 8,
      studentsCanCreateGroups: true,
      studentsCanLeaveGroups: true,
      groupLeaderCanDissolve: true,
      groupLeaderCanRemoveMembers: true,
      groupLeaderCanChangeMode: true,
      groupLeaderCanTransferLeadership: true,
      supervisorCanEditGroups: false,
    },
    archived: false,
    dueDate: '2026-05-15T00:00:00Z',
    createdAt: '2026-04-01T00:00:00Z',
    updatedAt: '2026-04-01T00:00:00Z',
  },
  {
    id: '2',
    courseId: '1',
    name: 'Implementação de Consultas SQL Avançadas',
    description:
      'Neste trabalho, cada grupo deverá desenvolver um conjunto de consultas SQL avançadas sobre um banco de dados relacional fornecido pelo professor.',
    assignmentFlags: {
      maxGroupMembers: 5,
      maxGroups: 8,
      studentsCanCreateGroups: false,
      studentsCanLeaveGroups: true,
      groupLeaderCanDissolve: false,
      groupLeaderCanRemoveMembers: true,
      groupLeaderCanChangeMode: true,
      groupLeaderCanTransferLeadership: true,
      supervisorCanEditGroups: true,
    },
    archived: false,
    dueDate: '2026-05-28T00:00:00Z',
    createdAt: '2026-04-01T00:00:00Z',
    updatedAt: '2026-04-01T00:00:00Z',
  },
]

export const artifactsMock: AssignmentArtifact[] = [
  {
    id: '1',
    assignmentId: '1',
    name: 'Documentação oficial do PostgreSQL',
    description: 'postgresql.org/docs/current',
    resourceLink: 'https://postgresql.org/docs/current',
    createdAt: '2026-04-01T00:00:00Z',
    updatedAt: '2026-04-01T00:00:00Z',
  },
  {
    id: '2',
    assignmentId: '1',
    name: 'Tutorial de SQL Avançado - W3Schools',
    description: 'w3schools.com/sql/sql_join.asp',
    resourceLink: 'https://w3schools.com/sql/sql_join.asp',
    createdAt: '2026-04-01T00:00:00Z',
    updatedAt: '2026-04-01T00:00:00Z',
  },
]
