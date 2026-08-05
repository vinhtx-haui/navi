<div align="center">

<!-- Logo placeholder — thay bằng assets/logo.svg khi có brand identity -->
<img src="docs/assets/logo-placeholder.svg" alt="Navi" width="96" height="96" />

# Navi

**A personal growth navigation system for university students.**

*Understand where you are, where you should go next, and why.*

[![Status](https://img.shields.io/badge/status-Phase%200%20·%20Foundation-blue)](docs/roadmap.md)
[![Docs](https://img.shields.io/badge/docs-vision%20%26%20architecture-informational)](docs/)
[![License](https://img.shields.io/badge/license-TBD-lightgrey)](#license)

</div>

---

## What is Navi?

Most students do not fail because they lack information. They fail because information is
scattered, unverified, and disconnected from their actual situation. A student in their second
year can find a thousand roadmaps online and still not know which course to prioritise this
semester, whether they are behind, or what a "backend developer" role actually requires.

Navi is a platform that answers three questions continuously, using the student's own data:

| Question | What Navi does |
| --- | --- |
| **Where am I?** | Consolidates courses, credits, GPA, completed skills and finished goals into one honest picture. |
| **Where should I go next?** | Turns a long-term target into an ordered, achievable set of next steps. |
| **Why this next?** | Explains every recommendation, with its source and its reasoning made visible. |

Navi is **not** another to-do list or GPA calculator. Those are features. The product is
navigation: reducing the distance between *where a student is* and *where they intend to be*.

## Vision

> Every student should be able to see their own progress clearly, and know their next step
> without having to guess.

Long term, Navi grows from a personal academic tracker into a growth navigation system that
spans an entire university journey and the transition into a first job — study planning,
skill roadmaps, career direction, and a community of people one or two steps ahead.

Full vision: [docs/vision.md](docs/vision.md)

## Core Values

These are engineering constraints, not slogans. Each one is enforced somewhere in the design.

### 1. Trust — Chữ Tín
No unverified information reaches the user. Every piece of curriculum, skill, or career data
carries its source and its status (`verified` / `community` / `unverified`), and the UI shows
that status. When Navi does not know something, it says so instead of guessing.

### 2. Speed — Tốc độ
The measure is *time to answer*, not just server latency. A student should reach the
information they need in seconds, not after an evening of searching forums.

### 3. Innovation & Creativity — Đổi mới và sáng tạo
Technology is used where it changes the experience — personalised sequencing, progress
insight, and later an AI assistant grounded in the student's real data — never as decoration.

### 4. Practicality — Thực tiễn
A feature ships only if it plausibly helps a student make real progress in study or career.
Metrics that look impressive but change no behaviour are not shipped.

## Roadmap

| Phase | Theme | Goal |
| --- | --- | --- |
| **1** | **Foundation** | An honest, complete picture of the student's academic present. Courses, credits, progress, goals, skill roadmaps. |
| **2** | **Intelligence** | Navi begins to advise — personalised recommendations, progress insight, and an AI assistant grounded in real data. |
| **3** | **Community** | Verified knowledge from students who already walked the path; shared roadmaps and peer review. |
| **4** | **Career Growth** | The bridge from university to first job — skill-to-role mapping, portfolio, and mentor network. |

Detail and success criteria per phase: [docs/roadmap.md](docs/roadmap.md)

## Tech Stack

> Status: proposed for Phase 1. Recorded as decisions in [docs/adr/](docs/adr/) so that later
> changes are traceable rather than silent.

| Layer | Choice | Note |
| --- | --- | --- |
| Backend | Java 21 · Spring Boot 3.5.3 | Modular monolith, module-per-domain |
| API | REST + OpenAPI 3 | Contract-first; GraphQL not planned for Phase 1 |
| Database | PostgreSQL 16 | Single source of truth, relational core |
| Migrations | Flyway | Versioned SQL in `backend/src/main/resources/db/migration/` |
| Cache / jobs | Redis 7 | Behind a Compose profile until a real need appears |
| Frontend | Next.js 16 · React 19 · TypeScript · Tailwind 4 | Deliberately thin; backend is the centre of gravity |
| Auth | Spring Security + JWT | Refresh-token rotation — not yet implemented |
| Infra | Docker Compose (dev) → single VPS/container host (prod) | Kubernetes explicitly out of scope for now |
| Quality | JUnit 5 · Testcontainers · ArchUnit | Integration tests run against real PostgreSQL |

## Project Structure

```
navi-platform/
├── docs/            Product and engineering documentation (source of truth)
│   └── adr/         Architecture Decision Records
├── backend/         Spring Boot modular monolith
├── frontend/        Next.js web client
├── database/        Seed data, ERD (migrations ship with the backend)
└── infra/           Docker Compose, deployment, CI notes
```

## Getting Started

Requires JDK 21, Node 22+, and Docker.

```bash
docker compose -f infra/docker/docker-compose.dev.yml up -d
```

```bash
cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

```bash
cd frontend && npm run dev
```

The API serves `http://localhost:8080`, the web client `http://localhost:3000`. Flyway applies
migrations on backend startup. Run the test suite — including integration tests against a real
PostgreSQL container — with `cd backend && ./mvnw verify`.

## Development Status

**Phase 1 — Foundation, in progress.** The skeleton runs end to end: the web client renders live
data fetched from the API, which reads a Flyway-migrated PostgreSQL database. No product feature
has been built yet.

Documentation came first on purpose: Navi is a long-term project, and decisions made in week one
about domain boundaries and data ownership are the expensive ones to reverse.

- [x] Vision, product requirements, roadmap
- [x] Architecture and initial ADRs
- [x] Backend skeleton — shared kernel, module packages, config, meta endpoint
- [x] Database baseline migration — module schemas + `knowledge.sources`
- [x] Architecture tests enforcing module boundaries (ArchUnit)
- [x] Frontend shell — typed API client, live backend status
- [ ] Auth (register / login / refresh)
- [ ] Academic module — courses & enrollments
- [ ] Progress module — credits & GPA
- [ ] Goals and skill roadmaps
- [ ] CI pipeline

## Documentation

| Document | Contents |
| --- | --- |
| [vision.md](docs/vision.md) | Problem, target users, long-term vision, value delivered |
| [product-requirements.md](docs/product-requirements.md) | Personas, use cases, functional requirements |
| [architecture.md](docs/architecture.md) | High-level architecture, modules, data flow |
| [roadmap.md](docs/roadmap.md) | Four phases with goals and success criteria |
| [glossary.md](docs/glossary.md) | Shared domain vocabulary (VI/EN) |
| [adr/](docs/adr/) | Why each significant technical decision was made |

## License

Not yet chosen. Until a `LICENSE` file exists, all rights are reserved by the author.

---

<div align="center">
<sub>Navi — started 2026. Built in public, one honest step at a time.</sub>
</div>
