# Development Guide

> This document defines the engineering practices, development workflow, and coding standards followed throughout the CivicOS project.

---

## 1. Purpose

The purpose of this document is to establish a consistent development process for CivicOS. It defines how new features are planned, implemented, tested, reviewed, and deployed, ensuring that the project remains maintainable and production-ready as it evolves.

Although CivicOS is currently developed by a single developer, the workflow intentionally follows professional software engineering practices commonly used in collaborative development teams.

## 2. Development Workflow

Every new feature follows the same development lifecycle to ensure consistency and maintainability throughout the project.

Feature Idea
    ↓
GitHub Issue
    ↓
Feature Branch
    ↓
Implementation
    ↓
Automated Testing
    ↓
Documentation Update (if needed)
    ↓
Pull Request
    ↓
Merge to Main
    ↓
CI/CD Pipeline
    ↓
Deployment

Each feature begins with a GitHub Issue describing the required functionality. Development is performed on an isolated feature branch following the project's branch naming conventions.

Before merging into the main branch, the implementation should be tested, relevant documentation should be updated, and all automated checks must pass successfully.

Once merged, the application should remain deployable at all times.

## 3. Git Workflow

CivicOS follows a simplified GitHub Flow workflow.

The `main` branch always represents the latest stable version of the project.

Every new feature, bug fix, refactor, or documentation update must be developed on a separate branch and merged into `main` through a Pull Request after all quality checks have been completed.

## 4. Branch Naming

Every change must be developed in its own branch.

Branch names should clearly describe the purpose of the work and reference the related GitHub Issue whenever possible.

Examples:

- feature/jwt-authentication
- feature/incident-management
- fix/login-validation
- docs/update-prd
- refactor/auth-service

Whenever possible, branches should be created directly from the related GitHub Issue to maintain traceability.

## 5. Commit Convention

All commits follow the Conventional Commits specification.

Examples:

- feat(auth): implement JWT authentication
- fix(api): validate incident status
- docs(prd): update project goals
- refactor(web): simplify dashboard layout
- test(auth): add authentication integration tests
- chore(ci): configure GitHub Actions

## 6. Pull Requests

All changes must be merged through a Pull Request.

Before merging, every Pull Request should:

- Reference the related GitHub Issue.
- Pass all automated CI checks.
- Include updated documentation when applicable.

## 7. Definition of Done

A task is considered complete only if:

- The implementation is finished.
- The code follows the project's coding standards.
- Relevant automated tests have been added or updated.
- Documentation has been updated when required.
- All CI checks pass successfully.
- The feature has been manually verified.
- The feature is deployable.
- The related GitHub Issue has been closed.

## 8. Coding Principles

The following principles should guide all implementation decisions:

- Prefer readability over cleverness.
- Keep functions and classes focused on a single responsibility.
- Avoid duplicated logic whenever possible.
- Write self-explanatory code before relying on comments.
- Favor composition over inheritance where appropriate.
- Build for maintainability rather than premature optimization.

## 9. Documentation

Documentation is maintained alongside the codebase.

Whenever a feature introduces architectural changes, business rules, or development workflow updates, the corresponding documentation should be updated within the same Pull Request whenever possible.

The documentation should always reflect the current state of the project.
