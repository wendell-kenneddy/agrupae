import type { AxiosError } from 'axios'

const errorTranslations: Record<string, string> = {
  // Domain exceptions & Assignment validations
  "Due date cannot be before assignment creation timestamp.": "A data de entrega não pode ser anterior à data de criação do trabalho.",
  "Due date must be in the future.": "A data de entrega deve ser no futuro.",
  "Assignment name cannot be blank.": "O nome do trabalho não pode estar em branco.",
  "Assignment description cannot be null.": "A descrição do trabalho não pode ser nula.",
  "Due date cannot be null.": "A data de entrega não pode ser nula.",
  "Assignment flags cannot be null.": "As configurações do trabalho não podem ser nulas.",
  "Assignment is already archived.": "O trabalho já está arquivado.",
  "Cannot edit an archived assignment.": "Não é possível editar um trabalho arquivado.",

  // Flags combinations (ForbiddenFlagCombination)
  "FORBIDDEN: maxGroupMembers must be greater than zero.": "O limite máximo de membros por grupo deve ser maior que zero.",
  "FORBIDDEN: maxGroups must be greater than zero.": "O limite máximo de grupos deve ser maior que zero.",
  "FORBIDDEN: either studentsCanCreateGroups or supervisorCanEditGroups must be true.": "Permita que alunos criem grupos ou que o orientador gerencie grupos.",
  "FORBIDDEN: studentsCanLeaveGroups must be true when groupLeaderCanDissolve is true.": "Alunos devem poder sair do grupo se o líder puder dissolvê-lo.",

  // User / Auth
  "User not found.": "Usuário não encontrado.",
  "User already exists.": "Usuário já cadastrado.",
  "Email is already in use.": "Este e-mail já está sendo utilizado.",
  "Invalid refresh token.": "Sessão expirada. Faça login novamente.",
  "Refresh token has been revoked.": "Sessão revogada. Faça login novamente.",
  "Refresh token has expired.": "Sessão expirada. Faça login novamente.",
  "Invalid email or password.": "E-mail ou senha inválidos.",

  // Course
  "Invalid invite code.": "Código de convite inválido.",
  "Course not found.": "Disciplina não encontrada.",
  "Only the class leader or an admin can archive this course.": "Apenas o líder da disciplina ou um administrador pode arquivá-la.",
  "Course leader cannot join their own course as a student.": "O líder da disciplina não pode participar como aluno.",
  "User has already joined this course.": "Você já faz parte desta disciplina.",
  "It's only possible to transfer leadership of a course to a member.": "Só é possível transferir a liderança da disciplina para um membro dela.",
  "Only the course leader can transfer leadership of this course.": "Apenas o líder da disciplina pode transferir sua liderança.",

  // Assignment
  "Only the class leader or an admin can archive this assignment.": "Apenas o líder da disciplina ou um administrador pode arquivar este trabalho.",
  "Only the class leader or an admin can edit this assignment.": "Apenas o líder da disciplina ou um administrador pode editar este trabalho.",
  "You are not authorized to delete this assignment.": "Você não tem autorização para excluir este trabalho.",
  "Assignment not found.": "Trabalho não encontrado.",

  // Group
  "Student already belongs to a group in this assignment.": "O aluno já pertence a um grupo neste trabalho.",
  "The group leader cannot remove themselves; use the leave group action instead.": "O líder do grupo não pode se remover; saia do grupo em vez disso.",
  "A pending entry request already exists for this assignment.": "Já existe uma solicitação pendente para este trabalho.",
  "Only the group leader can perform this action.": "Apenas o líder do grupo pode realizar esta ação.",
  "Maximum number of groups for this assignment has been reached.": "O limite máximo de grupos para este trabalho foi atingido.",
  "Leaving a group is not allowed for this assignment.": "Não é permitido sair de grupos neste trabalho.",
  "Group is not open.": "O grupo não está aberto.",
  "Group not found.": "Grupo não encontrado.",
  "Group is not closed.": "O grupo não está fechado.",
  "Group mode changing is not allowed for this assignment.": "Não é permitido alterar o modo do grupo neste trabalho.",
  "Group member removal is not allowed for this assignment.": "Não é permitida a remoção de membros neste trabalho.",
  "Group member not found.": "Membro do grupo não encontrado.",
  "Max group member limit reached.": "Limite máximo de membros no grupo atingido.",
  "Entry request not found.": "Solicitação de entrada não encontrada.",
  "Group dissolution is not allowed for this assignment.": "Não é permitida a dissolução de grupos neste trabalho.",
  "Group creation is not allowed for this assignment.": "Não é permitida a criação de grupos neste trabalho.",
  "The assignment deadline has already passed.": "O prazo final do trabalho já passou.",
  "Cannot create group in an archived assignment.": "Não é possível criar um grupo em um trabalho arquivado.",
  "Only PENDING requests can be cancelled.": "Apenas solicitações PENDENTES podem ser canceladas.",
  "Only PENDING requests can be accepted/rejected.": "Apenas solicitações PENDENTES podem ser aceitas ou rejeitadas.",
  "User is already the leader of the group.": "O usuário já é o líder do grupo."
}

export function getErrorMessage(error: unknown): string {
  if (typeof error === 'string') {
    return errorTranslations[error] || error
  }

  const axiosError = error as AxiosError
  if (axiosError && axiosError.response) {
    const data = axiosError.response.data
    if (typeof data === 'string') {
      const trimmed = data.trim()
      if (errorTranslations[trimmed]) {
        return errorTranslations[trimmed]
      }
      if (trimmed.startsWith('<!DOCTYPE html>') || trimmed.startsWith('<html')) {
        return "Ocorreu um erro inesperado no servidor. Tente novamente."
      }
      return trimmed
    }
  }

  return "Ocorreu um erro inesperado. Tente novamente."
}
