# Navi Backend

Spring Boot modular monolith — the centre of gravity of the project. Read
[docs/architecture.md](../docs/architecture.md) before adding code.

**Status:** skeleton runs. Shared kernel, module packages, baseline migration and architecture tests
are in place. No product feature is implemented yet.

---

## Môi trường

| Yêu cầu | Trạng thái (2026-08-05) |
| --- | --- |
| JDK 21 | ✅ OpenJDK 21.0.12 tại `/opt/homebrew/opt/openjdk@21` |
| Maven | ✅ Wrapper `./mvnw` (3.9.11) — không cần Maven cài sẵn |
| Docker | ✅ 28.5.1 — cần cho PostgreSQL và Testcontainers |

Homebrew's `openjdk@21` là **keg-only**: nó không nằm trên `PATH` và `/usr/libexec/java_home`
không thấy nó. Vì vậy `export JAVA_HOME=...` **không** đủ cho lệnh `java` trực tiếp — `java` vẫn
là JDK 17 trên `PATH`. Hai cách dùng:

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
```

Dòng trên đủ cho `./mvnw` (wrapper đọc `JAVA_HOME`). Khi chạy jar trực tiếp thì gọi thẳng binary:

```bash
/opt/homebrew/opt/openjdk@21/bin/java -jar target/navi-backend-0.1.0-SNAPSHOT.jar
```

Thêm `export JAVA_HOME=/opt/homebrew/opt/openjdk@21` vào `~/.zshrc` để không phải lặp lại.

## Chạy

Cần PostgreSQL trước:

```bash
docker compose -f ../infra/docker/docker-compose.dev.yml up -d
```

```bash
JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Kiểm tra:

```bash
curl -s localhost:8080/actuator/health && curl -s localhost:8080/api/v1/meta
```

## Test

```bash
JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./mvnw verify
```

41 test, chia làm ba loại:

| Loại | Ví dụ | Cần gì |
| --- | --- | --- |
| Domain unit test | `CreditTest`, `GradeTest`, `ProvenanceTest`, `AnswerTest` | Không cần gì — chạy trong vài ms |
| Web layer test | `MetaControllerTest`, `ApiExceptionHandlerTest` | Spring context tối thiểu, không có database |
| Integration test | `BaselineMigrationTest` | **Docker** — Testcontainers dựng PostgreSQL 16 thật |
| Architecture test | `ModuleBoundaryTest` | Không cần gì |

## Cấu trúc hiện tại

```
backend/
├── pom.xml
├── src/main/java/com/navi/
│   ├── NaviApplication.java
│   ├── shared/                     # shared kernel — giữ càng nhỏ càng tốt
│   │   ├── domain/                 # Provenance, VerificationStatus, Credit, Grade, GpaScale,
│   │   │                           # Answer<T>, Ids
│   │   ├── error/                  # DomainException, ResourceNotFound, BusinessRuleViolation,
│   │   │                           # ApiExceptionHandler
│   │   ├── event/                  # DomainEvent
│   │   └── api/                    # MetaController, RequestIdFilter
│   ├── identity/                   # ─┐
│   ├── academic/                   #  │ package-info.java mô tả bounded context,
│   ├── progress/                   #  │ dữ liệu sở hữu, và quy tắc biên giới.
│   ├── goal/                       #  │ Chưa có code nghiệp vụ.
│   ├── skill/                      #  │
│   └── knowledge/                  # ─┘
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   └── db/migration/V1__baseline.sql
└── src/test/java/com/navi/
    ├── architecture/ModuleBoundaryTest.java
    ├── BaselineMigrationTest.java
    └── shared/...
```

## Quy tắc bắt buộc

Ba quy tắc đầu được `ModuleBoundaryTest` kiểm trong mỗi lần build — vi phạm làm build đỏ kèm thông
báo chỉ rõ ADR liên quan:

1. **`domain` không import Spring, JPA, Hibernate, servlet API.** Domain logic là Java thuần.
2. **Module chỉ gọi module khác qua `XModuleApi`** — không import `domain` của module khác, không
   truy vấn bảng thuộc module khác.
3. **Không có phụ thuộc vòng giữa các module.**
4. **`@Transactional` chỉ ở tầng `application`** — không ở controller, không ở domain.
5. **Constructor injection**, không `@Autowired` trên field.

Ngoài ra:

- **Không dùng `ddl-auto: update`** ở bất kỳ profile nào. Schema do Flyway định nghĩa; JPA chỉ
  `validate`.
- **Mọi bảng tri thức phải có `source_id` + `verification_status`**, và `verification_status`
  **không có DEFAULT**. Xem comment cuối `V1__baseline.sql`.
- **Không commit secret.** `application-local.yml` và `.env` đã bị `.gitignore` loại trừ.
- **Kiểm quyền ở tầng service**, không chỉ ở controller. Mọi truy vấn dữ liệu người dùng ràng buộc
  theo `userId` đang đăng nhập.

## Migration

Migration nằm ở `src/main/resources/db/migration/` (không phải `database/migrations/` như bản
tài liệu đầu tiên dự kiến). Lý do: Flyway chạy lúc app khởi động và đọc từ classpath, nên đặt trong
resources làm jar tự chứa được toàn bộ schema. Đặt ngoài `backend/` sẽ buộc bản deploy mang thêm
file rời — một cách để môi trường lệch nhau.

Quy ước: `V<n>__<mô_tả>.sql`, số tăng dần, **không sửa migration đã chạy** — sai thì viết migration
mới. Xem [database/README.md](../database/README.md).

## Bước tiếp theo

Theo thứ tự trong [docs/roadmap.md](../docs/roadmap.md) Phase 1:

1. **Module `identity`** — register / login / refresh với Spring Security + JWT. Khi thêm
   `spring-boot-starter-security`, mọi endpoint bị chặn theo mặc định; cần cấu hình cho phép
   `/actuator/health` và `/api/v1/meta`.
2. **springdoc-openapi** — sinh OpenAPI spec để frontend generate type thay vì viết tay.
3. **Module `academic`** — `Course` (định nghĩa môn) tách khỏi `Enrollment` (một người học môn đó).
4. **Module `progress`** — tính tín chỉ và GPA. Domain logic thuần, unit test kỹ; đây là nơi
   `Answer<T>` được dùng thật.
