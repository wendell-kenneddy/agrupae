-- =============================================================================
-- SEED: Dados de demonstração — Curso de Ciência da Computação
-- =============================================================================
-- Todos os usuários usam a senha: 12345678
-- Hash BCrypt (strength 10): $2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu
--
-- O líder da turma (supervisor_id) é o Prof. Ricardo.
-- O invite_code é um UUID, como gerado por Course.create().
-- Os UUIDs são aleatórios, simulando dados reais criados pelo frontend.
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. USUÁRIOS
-- ─────────────────────────────────────────────────────────────────────────────

-- Professor / Líder da turma (ADMIN)
INSERT INTO users (id, "name", email, password_hash, "role") VALUES
    ('28a7801d-eed0-44a8-9dc8-e8196aa9ca72', 'Prof. Ricardo Takahashi', 'prof.ricardo@gmail.com', '$2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu', 'ADMIN');

-- Alunos (USER)
INSERT INTO users (id, "name", email, password_hash, "role") VALUES
    ('937ae0f2-460e-4b73-aa06-f4b03daf262c', 'Lucas Silva',         'aluno1@gmail.com',  '$2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu', 'USER'),
    ('3f4e1a98-5266-4cb5-ae71-8e2ba6bd1df4', 'Marina Oliveira',     'aluno2@gmail.com',  '$2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu', 'USER'),
    ('cde6146c-7068-4114-a806-e6220682bd42', 'Pedro Santos',        'aluno3@gmail.com',  '$2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu', 'USER'),
    ('a3f1ea58-74a3-4c02-8d32-231e76c7be18', 'Ana Costa',           'aluno4@gmail.com',  '$2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu', 'USER'),
    ('7573b8f1-a03b-4434-80aa-51ee11b2a099', 'Gabriel Ferreira',    'aluno5@gmail.com',  '$2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu', 'USER'),
    ('fef5e0d4-882d-428e-817f-9c89ac905de5', 'Juliana Almeida',     'aluno6@gmail.com',  '$2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu', 'USER'),
    ('d5716850-2133-4bc8-8e31-dfae190fd446', 'Rafael Souza',        'aluno7@gmail.com',  '$2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu', 'USER'),
    ('13b936f8-083e-4ac4-9eaa-0b740f921627', 'Camila Rodrigues',    'aluno8@gmail.com',  '$2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu', 'USER'),
    ('79fccf96-38e6-44e5-be52-0958c11cc3fb', 'Bruno Lima',          'aluno9@gmail.com',  '$2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu', 'USER'),
    ('b35a83a6-db81-4ca7-ab1d-538102af95b9', 'Fernanda Pereira',    'aluno10@gmail.com', '$2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu', 'USER'),
    ('7edddc6f-6990-450c-86af-2d5d5c2a0ac7', 'Thiago Ribeiro',     'aluno11@gmail.com', '$2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu', 'USER'),
    ('e42556af-b749-4ee3-b01c-0d147c9d3952', 'Isabela Mendes',      'aluno12@gmail.com', '$2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu', 'USER'),
    ('5217bac8-cf2f-4d1c-9396-2e4d745077ed', 'Diego Martins',       'aluno13@gmail.com', '$2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu', 'USER'),
    ('040b4281-65c0-4d10-8897-106e0b31c7f7', 'Larissa Barbosa',     'aluno14@gmail.com', '$2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu', 'USER'),
    ('2cb4209e-8933-41a3-9b9f-17e53eef9902', 'Matheus Gonçalves',   'aluno15@gmail.com', '$2a$10$kRRI2lYp2oQY5xDrSa06cutA.HwKptHeHUWcGoeTezfF4e4wOlshu', 'USER');

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. CURSO (turma) — líder = Prof. Ricardo, invite_code = UUID
-- ─────────────────────────────────────────────────────────────────────────────

INSERT INTO courses (id, supervisor_id, invite_code, "name", "description", archived) VALUES
    ('c826f004-7f59-4179-90ea-e2eb5cedc89e',
     '28a7801d-eed0-44a8-9dc8-e8196aa9ca72',
     '3f62a52d-4ec5-4dc3-949a-54a5290d3b84',
     'Ciência da Computação — Engenharia de Software',
     'Turma de Engenharia de Software do curso de Ciência da Computação. Semestre 2026.1.',
     FALSE);

-- ─────────────────────────────────────────────────────────────────────────────
-- 3. ARTEFATOS DO CURSO
-- ─────────────────────────────────────────────────────────────────────────────

INSERT INTO courses_artifacts (id, course_id, "name", "description", resource_link) VALUES
    ('7f3e84d3-70e3-4271-9f55-72c3d353cb74',
     'c826f004-7f59-4179-90ea-e2eb5cedc89e',
     'Plano de Ensino',
     'Plano de ensino completo da disciplina com cronograma e bibliografia.',
     'https://docs.google.com/document/d/exemplo-plano-ensino'),
    ('c5ee51e7-49e6-4903-b390-39936ff8d397',
     'c826f004-7f59-4179-90ea-e2eb5cedc89e',
     'Repositório da Disciplina',
     'Repositório com exemplos de código e materiais de apoio.',
     'https://github.com/exemplo/eng-software-2026');

-- ─────────────────────────────────────────────────────────────────────────────
-- 4. MATRÍCULA — líder + alunos no curso (líder é inserido como em CreateCourseService)
-- ─────────────────────────────────────────────────────────────────────────────

INSERT INTO users_courses (student_id, course_id) VALUES
    ('28a7801d-eed0-44a8-9dc8-e8196aa9ca72', 'c826f004-7f59-4179-90ea-e2eb5cedc89e'),
    ('937ae0f2-460e-4b73-aa06-f4b03daf262c', 'c826f004-7f59-4179-90ea-e2eb5cedc89e'),
    ('3f4e1a98-5266-4cb5-ae71-8e2ba6bd1df4', 'c826f004-7f59-4179-90ea-e2eb5cedc89e'),
    ('cde6146c-7068-4114-a806-e6220682bd42', 'c826f004-7f59-4179-90ea-e2eb5cedc89e'),
    ('a3f1ea58-74a3-4c02-8d32-231e76c7be18', 'c826f004-7f59-4179-90ea-e2eb5cedc89e'),
    ('7573b8f1-a03b-4434-80aa-51ee11b2a099', 'c826f004-7f59-4179-90ea-e2eb5cedc89e'),
    ('fef5e0d4-882d-428e-817f-9c89ac905de5', 'c826f004-7f59-4179-90ea-e2eb5cedc89e'),
    ('d5716850-2133-4bc8-8e31-dfae190fd446', 'c826f004-7f59-4179-90ea-e2eb5cedc89e'),
    ('13b936f8-083e-4ac4-9eaa-0b740f921627', 'c826f004-7f59-4179-90ea-e2eb5cedc89e'),
    ('79fccf96-38e6-44e5-be52-0958c11cc3fb', 'c826f004-7f59-4179-90ea-e2eb5cedc89e'),
    ('b35a83a6-db81-4ca7-ab1d-538102af95b9', 'c826f004-7f59-4179-90ea-e2eb5cedc89e'),
    ('7edddc6f-6990-450c-86af-2d5d5c2a0ac7', 'c826f004-7f59-4179-90ea-e2eb5cedc89e'),
    ('e42556af-b749-4ee3-b01c-0d147c9d3952', 'c826f004-7f59-4179-90ea-e2eb5cedc89e'),
    ('5217bac8-cf2f-4d1c-9396-2e4d745077ed', 'c826f004-7f59-4179-90ea-e2eb5cedc89e'),
    ('040b4281-65c0-4d10-8897-106e0b31c7f7', 'c826f004-7f59-4179-90ea-e2eb5cedc89e'),
    ('2cb4209e-8933-41a3-9b9f-17e53eef9902', 'c826f004-7f59-4179-90ea-e2eb5cedc89e');

-- ─────────────────────────────────────────────────────────────────────────────
-- 5. ASSIGNMENTS (Trabalhos) — variações de flags
-- ─────────────────────────────────────────────────────────────────────────────

-- Assignment 1: FREE — autonomia total dos alunos
INSERT INTO assignments (id, course_id, "name", "description", archived,
    max_group_members, max_group,
    students_can_create_groups, students_can_leave_groups,
    group_leader_can_dissolve, group_leader_can_remove_members,
    group_leader_can_change_mode, group_leader_can_transfer_leadership,
    course_supervisor_can_edit_assignment, due_date) VALUES
    ('1611fdd6-65bd-4fd8-87f5-d3b37528e0e7',
     'c826f004-7f59-4179-90ea-e2eb5cedc89e',
     'Projeto Final — Sistema Web',
     'Desenvolver um sistema web completo utilizando arquitetura MVC. O grupo deve entregar: documentação técnica, código-fonte no GitHub e apresentação final.',
     FALSE, 4, 5,
     TRUE,  TRUE,   -- students_can_create_groups, students_can_leave_groups
     TRUE,  TRUE,   -- group_leader_can_dissolve, group_leader_can_remove_members
     TRUE,  TRUE,   -- group_leader_can_change_mode, group_leader_can_transfer_leadership
     TRUE,           -- course_supervisor_can_edit_assignment
     '2026-08-15 23:59:00-03');

-- Assignment 2: Grupos fixos — alunos criam grupos mas não podem sair
INSERT INTO assignments (id, course_id, "name", "description", archived,
    max_group_members, max_group,
    students_can_create_groups, students_can_leave_groups,
    group_leader_can_dissolve, group_leader_can_remove_members,
    group_leader_can_change_mode, group_leader_can_transfer_leadership,
    course_supervisor_can_edit_assignment, due_date) VALUES
    ('6fb80aed-70c0-4025-97b0-9170d0547411',
     'c826f004-7f59-4179-90ea-e2eb5cedc89e',
     'Seminário — Padrões de Projeto',
     'Cada grupo deve apresentar um padrão de projeto (GoF) com exemplos práticos em Java. Incluir: slides, código de exemplo e exercício para a turma.',
     FALSE, 3, 6,
     TRUE,  FALSE,   -- students_can_create_groups, students_can_leave_groups
     FALSE, TRUE,    -- group_leader_can_dissolve, group_leader_can_remove_members
     TRUE,  FALSE,   -- group_leader_can_change_mode, group_leader_can_transfer_leadership
     TRUE,            -- course_supervisor_can_edit_assignment
     '2026-07-20 23:59:00-03');

-- Assignment 3: Líder forte — alunos criam, líder gerencia com mais controle
INSERT INTO assignments (id, course_id, "name", "description", archived,
    max_group_members, max_group,
    students_can_create_groups, students_can_leave_groups,
    group_leader_can_dissolve, group_leader_can_remove_members,
    group_leader_can_change_mode, group_leader_can_transfer_leadership,
    course_supervisor_can_edit_assignment, due_date) VALUES
    ('75043780-cec7-4744-a0f7-3c918b05d0be',
     'c826f004-7f59-4179-90ea-e2eb5cedc89e',
     'Trabalho de Banco de Dados — Modelagem',
     'Modelar e implementar um banco de dados relacional para um sistema de gestão hospitalar. Entregar: DER, modelo lógico, script SQL e relatório.',
     FALSE, 5, 3,
     TRUE,  TRUE,    -- students_can_create_groups, students_can_leave_groups
     TRUE,  TRUE,    -- group_leader_can_dissolve, group_leader_can_remove_members
     FALSE, TRUE,    -- group_leader_can_change_mode, group_leader_can_transfer_leadership
     TRUE,            -- course_supervisor_can_edit_assignment
     '2026-09-01 23:59:00-03');

-- ─────────────────────────────────────────────────────────────────────────────
-- 6. ARTEFATOS DOS ASSIGNMENTS
-- ─────────────────────────────────────────────────────────────────────────────

INSERT INTO assignment_artifacts (id, assignment_id, assignment_artifact_name, assignment_artifact_description, link) VALUES
    ('dafd9c8f-25ba-406c-aa9a-7902fe31b8c7',
     '1611fdd6-65bd-4fd8-87f5-d3b37528e0e7',
     'Especificação do Projeto Final',
     'Documento com requisitos mínimos, critérios de avaliação e formato de entrega.',
     'https://docs.google.com/document/d/exemplo-spec-projeto'),
    ('8541fba4-6acb-4658-b2c0-ff92d5288cc6',
     '6fb80aed-70c0-4025-97b0-9170d0547411',
     'Lista de Padrões GoF',
     'Lista dos padrões de projeto disponíveis para escolha com referências bibliográficas.',
     'https://docs.google.com/spreadsheets/d/exemplo-padroes-gof'),
    ('76121784-27bf-416d-9026-d8afabe94dfa',
     '75043780-cec7-4744-a0f7-3c918b05d0be',
     'Template do Relatório',
     'Template em LaTeX para o relatório de modelagem de banco de dados.',
     'https://www.overleaf.com/exemplo-template-relatorio');

-- ─────────────────────────────────────────────────────────────────────────────
-- 7. GRUPOS — todos criados por alunos (students_can_create_groups = TRUE)
-- ─────────────────────────────────────────────────────────────────────────────

-- === Assignment 1 (Free) — grupos criados pelos alunos ===

INSERT INTO groups (id, assignment_id, leader_id, "name", "open", members_can_edit_artifacts) VALUES
    ('41789c54-d6e1-432b-b127-ad76a7ea3f33',
     '1611fdd6-65bd-4fd8-87f5-d3b37528e0e7',
     '937ae0f2-460e-4b73-aa06-f4b03daf262c',
     'ByteForce', TRUE, TRUE),
    ('e663ff56-5e4b-44b7-9ed7-0c2b00e8e152',
     '1611fdd6-65bd-4fd8-87f5-d3b37528e0e7',
     '7573b8f1-a03b-4434-80aa-51ee11b2a099',
     'NullPointers', FALSE, TRUE),
    ('6fe30a1e-b2b4-4518-ba76-da72bb0eb2ff',
     '1611fdd6-65bd-4fd8-87f5-d3b37528e0e7',
     '79fccf96-38e6-44e5-be52-0958c11cc3fb',
     'StackOverflowers', TRUE, FALSE);

-- === Assignment 2 (Grupos fixos) — grupos criados pelos alunos, sem poder sair ===

INSERT INTO groups (id, assignment_id, leader_id, "name", "open", members_can_edit_artifacts) VALUES
    ('95fc90f2-2f89-4c73-8674-8e6b72c063d0',
     '6fb80aed-70c0-4025-97b0-9170d0547411',
     '3f4e1a98-5266-4cb5-ae71-8e2ba6bd1df4',
     'Observer & Strategy', FALSE, TRUE),
    ('3f83ed78-34d0-4477-951d-89f7a1eba24e',
     '6fb80aed-70c0-4025-97b0-9170d0547411',
     'fef5e0d4-882d-428e-817f-9c89ac905de5',
     'Factory & Singleton', TRUE, TRUE);

-- === Assignment 3 (Líder forte) — grupo criado por aluno, líder gerencia ===

INSERT INTO groups (id, assignment_id, leader_id, "name", "open", members_can_edit_artifacts) VALUES
    ('32698456-172d-4762-8861-bc57aeff0049',
     '75043780-cec7-4744-a0f7-3c918b05d0be',
     'cde6146c-7068-4114-a806-e6220682bd42',
     'Equipe Alpha', FALSE, TRUE);

-- ─────────────────────────────────────────────────────────────────────────────
-- 8. MEMBROS DOS GRUPOS
-- ─────────────────────────────────────────────────────────────────────────────

-- ByteForce (Free) — líder: Lucas (aluno1) + 3 membros
INSERT INTO groups_members (group_id, member_id) VALUES
    ('41789c54-d6e1-432b-b127-ad76a7ea3f33', '937ae0f2-460e-4b73-aa06-f4b03daf262c'),
    ('41789c54-d6e1-432b-b127-ad76a7ea3f33', '3f4e1a98-5266-4cb5-ae71-8e2ba6bd1df4'),
    ('41789c54-d6e1-432b-b127-ad76a7ea3f33', 'cde6146c-7068-4114-a806-e6220682bd42'),
    ('41789c54-d6e1-432b-b127-ad76a7ea3f33', 'a3f1ea58-74a3-4c02-8d32-231e76c7be18');

-- NullPointers (Free) — líder: Gabriel (aluno5) + 2 membros
INSERT INTO groups_members (group_id, member_id) VALUES
    ('e663ff56-5e4b-44b7-9ed7-0c2b00e8e152', '7573b8f1-a03b-4434-80aa-51ee11b2a099'),
    ('e663ff56-5e4b-44b7-9ed7-0c2b00e8e152', 'fef5e0d4-882d-428e-817f-9c89ac905de5'),
    ('e663ff56-5e4b-44b7-9ed7-0c2b00e8e152', 'd5716850-2133-4bc8-8e31-dfae190fd446');

-- StackOverflowers (Free) — líder: Bruno (aluno9) + 2 membros
INSERT INTO groups_members (group_id, member_id) VALUES
    ('6fe30a1e-b2b4-4518-ba76-da72bb0eb2ff', '79fccf96-38e6-44e5-be52-0958c11cc3fb'),
    ('6fe30a1e-b2b4-4518-ba76-da72bb0eb2ff', 'b35a83a6-db81-4ca7-ab1d-538102af95b9'),
    ('6fe30a1e-b2b4-4518-ba76-da72bb0eb2ff', '7edddc6f-6990-450c-86af-2d5d5c2a0ac7');

-- Observer & Strategy (Grupos fixos) — líder: Marina (aluno2) + 2 membros
INSERT INTO groups_members (group_id, member_id) VALUES
    ('95fc90f2-2f89-4c73-8674-8e6b72c063d0', '3f4e1a98-5266-4cb5-ae71-8e2ba6bd1df4'),
    ('95fc90f2-2f89-4c73-8674-8e6b72c063d0', 'a3f1ea58-74a3-4c02-8d32-231e76c7be18'),
    ('95fc90f2-2f89-4c73-8674-8e6b72c063d0', '13b936f8-083e-4ac4-9eaa-0b740f921627');

-- Factory & Singleton (Grupos fixos) — líder: Juliana (aluno6) + 2 membros
INSERT INTO groups_members (group_id, member_id) VALUES
    ('3f83ed78-34d0-4477-951d-89f7a1eba24e', 'fef5e0d4-882d-428e-817f-9c89ac905de5'),
    ('3f83ed78-34d0-4477-951d-89f7a1eba24e', 'b35a83a6-db81-4ca7-ab1d-538102af95b9'),
    ('3f83ed78-34d0-4477-951d-89f7a1eba24e', 'e42556af-b749-4ee3-b01c-0d147c9d3952');

-- Equipe Alpha (Líder forte) — líder: Pedro (aluno3) + 4 membros
INSERT INTO groups_members (group_id, member_id) VALUES
    ('32698456-172d-4762-8861-bc57aeff0049', 'cde6146c-7068-4114-a806-e6220682bd42'),
    ('32698456-172d-4762-8861-bc57aeff0049', 'd5716850-2133-4bc8-8e31-dfae190fd446'),
    ('32698456-172d-4762-8861-bc57aeff0049', '7edddc6f-6990-450c-86af-2d5d5c2a0ac7'),
    ('32698456-172d-4762-8861-bc57aeff0049', '5217bac8-cf2f-4d1c-9396-2e4d745077ed'),
    ('32698456-172d-4762-8861-bc57aeff0049', '2cb4209e-8933-41a3-9b9f-17e53eef9902');

-- ─────────────────────────────────────────────────────────────────────────────
-- 9. ENTRY REQUESTS (pedidos de entrada em grupos fechados)
-- ─────────────────────────────────────────────────────────────────────────────

-- Camila (aluno8) pediu para entrar no NullPointers (fechado) — PENDING
INSERT INTO groups_entry_requests (id, group_id, user_id, group_entry_request_status) VALUES
    ('915abfe9-7234-4246-bcec-17069ab5c61b',
     'e663ff56-5e4b-44b7-9ed7-0c2b00e8e152',
     '13b936f8-083e-4ac4-9eaa-0b740f921627',
     'PENDING');

-- Isabela (aluno12) pediu para entrar no NullPointers (fechado) — REJECTED
INSERT INTO groups_entry_requests (id, group_id, user_id, group_entry_request_status) VALUES
    ('ebdfd80c-6cc7-4690-b99b-78c3877aef7b',
     'e663ff56-5e4b-44b7-9ed7-0c2b00e8e152',
     'e42556af-b749-4ee3-b01c-0d147c9d3952',
     'REJECTED');

-- Larissa (aluno14) pediu para entrar no Observer & Strategy (fechado) — ACCEPTED
INSERT INTO groups_entry_requests (id, group_id, user_id, group_entry_request_status) VALUES
    ('1676e537-49c1-466b-82d2-e67a1bad907f',
     '95fc90f2-2f89-4c73-8674-8e6b72c063d0',
     '040b4281-65c0-4d10-8897-106e0b31c7f7',
     'ACCEPTED');

-- ─────────────────────────────────────────────────────────────────────────────
-- 10. ARTEFATOS DOS GRUPOS
-- ─────────────────────────────────────────────────────────────────────────────

-- ByteForce — artefatos do projeto
INSERT INTO groups_artifacts (id, group_id, "name", "description", resource_link, private_artifact, deliverable, delivered_at) VALUES
    ('1d816c5c-d518-4a8f-beaa-f6fd0da69276',
     '41789c54-d6e1-432b-b127-ad76a7ea3f33',
     'Repositório GitHub',
     'Repositório principal do projeto com código-fonte e documentação.',
     'https://github.com/byteforce/projeto-final',
     FALSE, FALSE, NULL),
    ('a8e23662-e09d-4bde-8082-c3d86fee93b5',
     '41789c54-d6e1-432b-b127-ad76a7ea3f33',
     'Board do Trello',
     'Quadro Kanban para organização das tarefas do grupo.',
     'https://trello.com/b/exemplo-byteforce',
     TRUE, FALSE, NULL);

-- NullPointers — artefato do projeto
INSERT INTO groups_artifacts (id, group_id, "name", "description", resource_link, private_artifact, deliverable, delivered_at) VALUES
    ('7b669fa6-473c-4a10-9a0f-edb3c82f5fd9',
     'e663ff56-5e4b-44b7-9ed7-0c2b00e8e152',
     'Documento de Requisitos',
     'Levantamento de requisitos e casos de uso do sistema.',
     'https://docs.google.com/document/d/exemplo-req-nullpointers',
     FALSE, FALSE, NULL);

-- Observer & Strategy — artefato do seminário (já entregue)
INSERT INTO groups_artifacts (id, group_id, "name", "description", resource_link, private_artifact, deliverable, delivered_at) VALUES
    ('ebd74d39-c108-4f27-a392-420261bc0703',
     '95fc90f2-2f89-4c73-8674-8e6b72c063d0',
     'Slides da Apresentação',
     'Slides sobre os padrões Observer e Strategy com exemplos em Java.',
     'https://docs.google.com/presentation/d/exemplo-observer-strategy',
     FALSE, TRUE, '2026-07-10 14:30:00-03');

-- Equipe Alpha — artefato do trabalho de BD
INSERT INTO groups_artifacts (id, group_id, "name", "description", resource_link, private_artifact, deliverable, delivered_at) VALUES
    ('fefa1bca-4a76-4c5d-b8e3-cd87c66a03d0',
     '32698456-172d-4762-8861-bc57aeff0049',
     'Diagrama ER — v1',
     'Primeira versão do diagrama entidade-relacionamento do sistema hospitalar.',
     'https://drive.google.com/file/d/exemplo-der-alpha',
     FALSE, FALSE, NULL);
