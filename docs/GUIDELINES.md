# ***Project Guidelines***

## **Commits**

- Commits must follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) format;
- Commits should group related diffs together;
- Commits should not mix unrelated changes;
- Commits to the main branch are forbidden.
- Branch names must follow the pattern `<type>/<subject>`:
  - `<type>` is the type of change, following conventional commits types;
  - `<subject>` is a short description of the change in kebab-case.
    - Exemple: feat/add-user-authentication.
- New feature should be accompanied by tests.

## **Documentation**

- Changes such as refactoring and adding new features, or mutating the project's behavior must be documented;
- Each documentation artifact must include a changelog section, following the [Keep a Changelong](https://keepachangelog.com/en/1.1.0/) format;
- Documentation artifacts should be in markdown.

## **AI Usage**

- Permitted, no need to clarify in commits;
- Code quality is the metric for any contribution, regardless of AI used or not;