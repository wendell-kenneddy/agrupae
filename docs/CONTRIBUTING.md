# ***Contribution Guidelines***

## **Commits**

- Commits must follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) format;
- Commits should group related diffs together;
- Commits should not mix unrelated changes;
- Commits to the main branch are forbidden.
- Branch names must follow the pattern `<type>/<subject>`:
  - `<type>` is the type of change, following conventional commits types;
  - `<subject>` is a short description of the change in kebab-case.
    - Exemple: feat/add-user-authentication.
- Features, refactors, fixes etc should be accompanied by approppriate tests.

## **Documentation**

- Changes such as refactoring and adding new features, or mutating the project's behavior must be documented in the [``CHANGELOG.md``](../CHANGELOG.md);
- Documentation artifacts should be in markdown;

## **AI Usage**

- Permitted, no need to clarify in commits;
- Code quality and tests are the metric for any contribution, regardless of AI used or not;