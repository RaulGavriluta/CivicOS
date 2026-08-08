# CivicOS Architecture

## 1. Project overview
- monorepo
- `apps/web`
- `apps/api`
- `docs`

## 2. Backend architecture
- Spring Boot 4.1
- Java 21
- Gradle
- PostgreSQL
- Docker Compose

## 3. Frontend architecture
- Next.js
- React
- TypeScript
- Tailwind CSS
- Bun

## 4. Feature-based architecture
- backend organized by feature
- frontend organized by feature where appropriate
- example: `health/`
- tests mirror production package structure
- don't create global `controller/`, `service/`, `repository/` folders
- shared components/utilities should only be extracted when they are actually shared

## 5. Backend testing strategy
- JUnit 5
- `@WebMvcTest` for controller/web tests
- `MockMvc`
- `@SpringBootTest` reserved for integration/application-context tests
- tests should not depend on PostgreSQL unless the test actually needs DB integration

## 6. Database / local development
- PostgreSQL runs through Docker Compose
- local PostgreSQL may already occupy port `5432`
- Docker may expose PostgreSQL through another host port
- Spring datasource configuration must match the configured host port
- database credentials are configured consistently between Spring and Docker Compose

## 7. Frontend testing
- frontend linting runs in CI
- frontend production build runs in CI
- feature-specific tests should be added as features require them

## 8. CI
- GitHub Actions
- Frontend CI
- Backend CI
- Backend CI runs `./gradlew build`
- `build` includes tests
- Backend CI required for `main`
- PR workflow

## 9. Development workflow

Issue  
→ feature branch  
→ implementation  
→ tests  
→ local build  
→ commit  
→ push  
→ CI  
→ PR  
→ merge

## 10. Current implementation
- Health endpoint
- `GET /api/health`
- returns `{"status":"UP"}`
- `HealthControllerTest`
- merged into `main`

## 11. Architectural principles
- feature-oriented organization
- small focused commits
- tests alongside features
- CI must remain green
- don't introduce infrastructure before needed
- don't create speculative folders or abstractions
- keep architecture consistent across backend and frontend
