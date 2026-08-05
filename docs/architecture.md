# Architecture — Kiến trúc hệ thống

- **Trạng thái:** Draft v1 (đề xuất cho Phase 1) · 2026-08-04
- **Quyết định chi tiết:** xem [adr/](adr/)
- **Tài liệu liên quan:** [product-requirements.md](product-requirements.md) · [roadmap.md](roadmap.md)

> Tài liệu này mô tả kiến trúc **được đề xuất**. Chưa có dòng code nào được viết tại thời điểm
> viết tài liệu. Mọi con số hiệu năng trong §7 là **mục tiêu**, không phải kết quả đo.

---

## 1. Nguyên tắc thiết kế

Bốn nguyên tắc dưới đây được suy ra trực tiếp từ Core Values và từ mục tiêu học tập của dự án.
Khi có tranh chấp giữa các lựa chọn kỹ thuật, dùng thứ tự này để quyết định.

| # | Nguyên tắc | Hệ quả cụ thể |
| --- | --- | --- |
| **1** | **Biên giới domain quan trọng hơn biên giới hạ tầng** | Chia hệ thống theo domain (course, goal, skill…) chứ không theo tầng kỹ thuật. Một monolith có module rõ ràng tốt hơn microservices có biên giới sai. |
| **2** | **Domain logic độc lập với framework** | Quy tắc nghiệp vụ (tính GPA, tính tiến độ, kiểm tra tiên quyết) là Java thuần, test được không cần Spring và không cần database. |
| **3** | **Trust là ràng buộc dữ liệu, không phải tính năng UI** | `source` và `verification_status` là **NOT NULL** ở tầng database, không phải trường tùy chọn ở tầng application. |
| **4** | **Chọn độ phức tạp muộn nhất có thể** | Không thêm message queue, cache, service riêng khi chưa có vấn đề thật cần giải. Mỗi thành phần hạ tầng phải trả giá bằng chi phí vận hành. |

---

## 2. High-Level Architecture

### 2.1. Kiến trúc chọn: Modular Monolith

```
┌──────────────────────────────────────────────────────────────────────────┐
│                             CLIENT LAYER                                 │
│                                                                          │
│   Next.js Web App (TypeScript)          [Phase 4] Mobile Client          │
│   • SSR cho trang public                                                 │
│   • Client-side cho dashboard đã đăng nhập                               │
└───────────────────────────────┬──────────────────────────────────────────┘
                                │ HTTPS · REST/JSON · JWT Bearer
                                ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                    BACKEND — Spring Boot Modular Monolith                │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────────────┐  │
│  │  API LAYER — REST Controllers · OpenAPI 3 · DTO · Bean Validation  │  │
│  │  Cross-cutting: Auth filter · Rate limit · Error handler · Tracing │  │
│  └────────────────────────────────────────────────────────────────────┘  │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────────────┐  │
│  │  DOMAIN MODULES — mỗi module là một bounded context                │  │
│  │                                                                    │  │
│  │   identity      academic      progress      goal       skill       │  │
│  │   ──────────    ──────────    ──────────    ────────   ──────────  │  │
│  │   user          curriculum    calculation   goal       skill       │  │
│  │   auth token    course        snapshot      subgoal    roadmap     │  │
│  │                 semester      insight                  proficiency │  │
│  │                                                                    │  │
│  │   knowledge (Phase 2+)   ai-assistant (P2)   community (P3)        │  │
│  │   career (P4)                                                      │  │
│  │                                                                    │  │
│  │   Mỗi module:  api/ (public port) │ domain/ │ application/ │ infra/│  │
│  └────────────────────────────────────────────────────────────────────┘  │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────────────┐  │
│  │  SHARED KERNEL — chỉ những gì thực sự dùng chung                   │  │
│  │  Domain primitives (Credit, Grade, GpaScale) · Error model ·        │  │
│  │  Source & VerificationStatus · Pagination · Domain event bus        │  │
│  └────────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────┬──────────────────────────────────────────┘
                                │
        ┌───────────────────────┼────────────────────────┐
        ▼                       ▼                        ▼
┌───────────────┐      ┌─────────────────┐      ┌──────────────────┐
│ PostgreSQL 16 │      │ Redis 7         │      │ [P2] AI Provider │
│ Source of     │      │ Cache · rate    │      │ Claude API       │
│ truth         │      │ limit · session │      │ (retrieval-based)│
│ Flyway        │      │ (thêm khi cần)  │      │                  │
└───────────────┘      └─────────────────┘      └──────────────────┘
```

### 2.2. Vì sao Modular Monolith, không phải Microservices

Đây là quyết định kiến trúc quan trọng nhất ở giai đoạn này. Chi tiết trong
[ADR-0001](adr/0001-modular-monolith.md); tóm tắt:

| Tiêu chí | Modular Monolith | Microservices |
| --- | --- | --- |
| Biên giới domain khi chưa hiểu rõ domain | Sửa được bằng refactor trong cùng codebase | Sửa sai biên giới = viết lại nhiều service + hợp đồng API |
| Toàn vẹn dữ liệu | Transaction ACID trong một database | Cần saga / eventual consistency ngay từ đầu |
| Chi phí vận hành cho 1 người dev | Một process, một database | Service discovery, distributed tracing, orchestration |
| Giá trị học tập | Học được thiết kế domain, transaction, tối ưu truy vấn | Học được distributed systems, nhưng trên nền domain chưa vững |
| Giá trị portfolio | *[Inference]* Một monolith có biên giới sạch, có ADR và test tốt thường được đánh giá cao hơn microservices bị chia sai — nhưng đây là phán đoán dựa trên thực hành phổ biến, không phải sự thật đo được | |

**Điều kiện để tách service về sau:** một module đạt ít nhất một trong các điều kiện — cần scale
độc lập, cần công nghệ khác (ví dụ Python cho ML), hoặc có nhịp thay đổi khác hẳn phần còn lại.
Vì các module đã giao tiếp qua port (interface) chứ không gọi trực tiếp vào nhau, việc tách sau
này là thay implementation của port bằng HTTP client — không phải viết lại domain.

### 2.3. Cấu trúc bên trong một module

Mỗi domain module theo bốn lớp, phụ thuộc chỉ hướng vào trong (hexagonal-lite):

```
backend/src/main/java/com/navi/academic/
│
├── api/                 ← Cổng vào. Controller, DTO, mapper.
│   ├── CourseController.java
│   └── dto/
│
├── application/         ← Điều phối use case. Transaction boundary ở đây.
│   ├── CourseService.java
│   └── command/
│
├── domain/              ← Java thuần. KHÔNG import Spring, KHÔNG import JPA.
│   ├── Course.java              (entity nghiệp vụ + quy tắc)
│   ├── Credit.java              (value object)
│   ├── PrerequisiteRule.java    (quy tắc thuần)
│   └── CourseRepository.java    (interface — port ra ngoài)
│
├── infrastructure/      ← Implementation của port. JPA, HTTP client, v.v.
│   ├── JpaCourseRepository.java
│   └── entity/CourseJpaEntity.java
│
└── AcademicModuleApi.java   ← Interface DUY NHẤT module khác được gọi
```

**Quy tắc phụ thuộc — được kiểm bằng test kiến trúc (ArchUnit), không chỉ bằng lời:**

1. `domain` không được phụ thuộc vào `api`, `application`, `infrastructure`, Spring, hay JPA.
2. Module A gọi module B **chỉ qua** `BModuleApi`, không import trực tiếp `domain` của B.
3. Không có phụ thuộc vòng giữa các module.

*[Inference]* Việc dùng ArchUnit để kiểm các quy tắc trên là thực hành phổ biến trong hệ sinh
thái Java; hiệu quả cụ thể trên dự án này chưa được kiểm chứng vì chưa có code.

---

## 3. Các module chính

### 3.1. Module map

| Module | Bounded context | Sở hữu dữ liệu | Phase |
| --- | --- | --- | --- |
| **identity** | Người dùng, xác thực, phiên | `users`, `refresh_tokens` | 1 |
| **academic** | Chương trình đào tạo, môn học, học kỳ | `curricula`, `courses`, `enrollments`, `semesters` | 1 |
| **progress** | Tính toán và ghi nhận tiến độ | `progress_snapshots` | 1 |
| **goal** | Mục tiêu và mục tiêu con | `goals`, `subgoals` | 1 |
| **skill** | Kỹ năng, roadmap, mức thành thạo | `skills`, `roadmaps`, `roadmap_steps`, `user_skills` | 1 |
| **knowledge** | Nguồn tri thức + trạng thái kiểm chứng | `sources`, `verifications` | 1 (lõi) → 3 (đầy đủ) |
| **recommendation** | Sinh gợi ý có giải thích | `recommendations`, `explanations` | 2 |
| **ai-assistant** | Trợ lý hỏi đáp neo vào dữ liệu thật | `conversations`, `messages` | 2 |
| **community** | Chia sẻ, review, kiểm chứng cộng đồng | `shared_roadmaps`, `contributions`, `reviews` | 3 |
| **career** | Vai trò nghề, yêu cầu kỹ năng, portfolio | `roles`, `role_requirements`, `portfolios` | 4 |

### 3.2. Nguyên tắc sở hữu dữ liệu

> **Mỗi bảng có đúng một module sở hữu. Chỉ module sở hữu được ghi vào bảng đó.**

Module khác cần dữ liệu thì đọc qua API của module sở hữu. Ví dụ: `progress` cần danh sách môn
đã hoàn thành → gọi `AcademicModuleApi.getCompletedCourses(userId)`, **không** tự viết truy vấn
vào bảng `courses`.

Đây là ràng buộc quan trọng nhất về mặt bảo trì dài hạn. Nó là lý do việc tách microservice sau
này khả thi, và cũng là ràng buộc dễ bị vi phạm nhất khi cần "làm nhanh".

### 3.3. Module `knowledge` — hiện thực hóa giá trị Trust

Module này tồn tại **vì Core Value đầu tiên**, không vì một yêu cầu tính năng. Nó cung cấp cho
toàn hệ thống một mô hình chung:

```java
// shared kernel — mọi entity tri thức đều mang thông tin này
public record Provenance(
    SourceId source,              // nguồn: trường đại học, job posting, đóng góp cộng đồng…
    VerificationStatus status,    // VERIFIED | COMMUNITY | UNVERIFIED
    Instant verifiedAt,           // thời điểm kiểm chứng — null nếu chưa
    String verifiedBy             // ai/quy trình nào kiểm chứng
) {}
```

Ràng buộc kèm theo:

- Ở tầng database: `source_id NOT NULL`, `verification_status NOT NULL`, **không có DEFAULT 'VERIFIED'**.
- Ở tầng API: mọi response chứa dữ liệu tri thức đều trả kèm `provenance`.
- Ở tầng UI: trạng thái kiểm chứng được hiển thị, không bị ẩn đi cho "gọn".
- Khi không có dữ liệu: trả về trạng thái `unknown` một cách tường minh, **không** trả 0 hay giá
  trị suy đoán. Một số 0 gây hiểu nhầm còn tệ hơn một ô trống.

### 3.4. Giao tiếp giữa các module

Phase 1 dùng gọi trực tiếp qua interface (in-process, đồng bộ). Có thêm một domain event bus
đơn giản (Spring `ApplicationEventPublisher`) cho các tác dụng phụ:

```
CourseCompleted (academic)
        │
        ├──▶ progress: tính lại snapshot tiến độ
        ├──▶ goal:     kiểm tra mục tiêu liên quan đã đạt chưa
        └──▶ skill:    cập nhật kỹ năng liên quan tới môn học
```

Lợi ích: `academic` không cần biết ai quan tâm tới sự kiện của nó. Khi tách service về sau, event
bus in-process được thay bằng message broker mà không phải sửa domain logic.

---

## 4. Luồng dữ liệu cơ bản

### 4.1. Luồng ghi — sinh viên hoàn thành một môn học

```
[1] Client                 POST /api/v1/courses/{id}/complete  { grade: 8.5 }
                                        │  JWT Bearer
                                        ▼
[2] Auth Filter            Xác thực JWT → userId. Sai/hết hạn → 401, dừng.
                                        │
                                        ▼
[3] CourseController       Validate DTO (điểm trong thang hợp lệ) → Command object
                                        │
                                        ▼
[4] CourseService          @Transactional MỞ ĐẦU
    (application)          • Kiểm quyền: môn này có thuộc userId không? Không → 403
                           • Nạp Course từ repository (port)
                                        │
                                        ▼
[5] Course (domain)        Quy tắc nghiệp vụ THUẦN:
                           • Môn đang ở trạng thái cho phép hoàn thành?
                           • Điểm hợp lệ theo thang của trường?
                           • Đã đủ môn tiên quyết?
                           → Vi phạm: ném domain exception (không phải HTTP exception)
                                        │
                                        ▼
[6] Repository (infra)     UPDATE courses SET status='COMPLETED', grade=8.5
                                        │
                                        ▼
[7] Event publish          CourseCompleted(userId, courseId, credits, grade)
                                        │
                           @Transactional KẾT THÚC — commit
                                        │
                    ┌───────────────────┼───────────────────┐
                    ▼                   ▼                   ▼
[8] progress          goal                skill
    tính lại snapshot  kiểm mục tiêu       cập nhật kỹ năng
    (sau commit)       liên quan           liên quan
                                        │
                                        ▼
[9] Response               200 OK — trạng thái môn + tiến độ đã cập nhật
```

Điểm đáng lưu ý về thiết kế:

- **Bước 5 là nơi có giá trị thật của hệ thống.** Nó là Java thuần → test được bằng unit test
  chạy trong milliseconds, không cần Spring context, không cần database.
- **Bước 4 là biên transaction duy nhất.** Không mở transaction trong controller, không mở trong domain.
- **Bước 8 chạy sau khi commit** (`@TransactionalEventListener(AFTER_COMMIT)`) — tác dụng phụ
  không được kéo theo transaction chính khi nó thất bại.

### 4.2. Luồng đọc — dashboard tiến độ

```
[1] GET /api/v1/dashboard  ──▶  Auth Filter  ──▶  DashboardController
                                                        │
[2] DashboardService (application, module progress)      │
        │                                                │
        ├──▶ AcademicModuleApi.getCompletedCourses(userId)   (không truy vấn trực tiếp)
        ├──▶ AcademicModuleApi.getCurriculum(userId)
        ├──▶ GoalModuleApi.getActiveGoals(userId)
        └──▶ SkillModuleApi.getSkillProgress(userId)
                                                         │
[3] ProgressCalculator (domain, thuần)                    │
        • tín chỉ hoàn thành / tín chỉ yêu cầu
        • GPA hiện tại + xu hướng theo học kỳ
        • nhóm môn còn thiếu
        • NẾU thiếu curriculum → trả UNKNOWN, KHÔNG suy đoán
                                                         │
[4] Response — kèm provenance của mọi số liệu:
        {
          "creditProgress": { "completed": 78, "required": 145,
                              "provenance": { "source": "curriculum-k19-se",
                                              "status": "VERIFIED",
                                              "verifiedAt": "2026-07-01" } },
          "gpa":            { "value": 3.42, "scale": "4.0",
                              "basedOn": 24, "note": "tính từ 24 môn đã nhập" },
          "graduationForecast": { "status": "UNKNOWN",
                                  "reason": "Chưa có chương trình đào tạo cho ngành này" }
        }
```

Trường `graduationForecast` ở trạng thái `UNKNOWN` kèm lý do là ví dụ cụ thể nhất của giá trị
*Trust* trong thiết kế API: **hệ thống nói rõ nó không biết, thay vì trả một con số trông có vẻ đúng.**

### 4.3. Luồng gợi ý (Phase 2) — mọi gợi ý phải có giải thích

```
Dữ liệu người dùng (progress · goal · skill)
        │
        ▼
RecommendationEngine (domain, thuần, deterministic)
        │  áp dụng rule + dữ liệu đã kiểm chứng từ module knowledge
        ▼
Recommendation {
    action:      "Học Database Systems học kỳ tới",
    reasoning:   [ "Mục tiêu của bạn: Backend Developer",
                   "Database là kỹ năng nền cho 4 bước tiếp theo trong roadmap",
                   "Bạn đã đủ điều kiện tiên quyết (Data Structures — hoàn thành HK3)" ],
    provenance:  { source: "roadmap-backend-v2", status: VERIFIED }
}
```

Ràng buộc: **`reasoning` không được rỗng.** Một gợi ý không giải thích được thì không được sinh
ra. Điều này được kiểm ở tầng domain, không phải ở tầng UI.

*[Speculation]* Thiết kế engine dạng rule-based deterministic trước khi dùng model xác suất là
lựa chọn thiên về khả năng giải thích. Hiệu quả thực tế của nó với người dùng Navi chưa được kiểm chứng.

---

## 5. Thiết kế dữ liệu

### 5.1. Nguyên tắc

| Nguyên tắc | Lý do |
| --- | --- |
| PostgreSQL là **source of truth** duy nhất | Dữ liệu học tập có quan hệ dày (môn ↔ tiên quyết ↔ curriculum ↔ kỹ năng) — quan hệ là mô hình đúng, không phải document |
| Mọi thay đổi schema qua **Flyway migration** có version | Có thể tái tạo schema từ đầu; không sửa tay trên database |
| Migration nằm trong **`backend/src/main/resources/db/migration/`** | Flyway chạy lúc app khởi động và đọc từ classpath. Đặt ngoài `backend/` sẽ khiến jar không tự chứa được migration, và bản deploy phải mang thêm file rời — một cách để môi trường lệch nhau. `database/` giữ ERD và seed data. |
| **Không dùng `ddl-auto: update`** ở bất kỳ môi trường nào | Schema do migration định nghĩa, không do ORM suy diễn |
| Khóa chính **UUID v7** | Không lộ thông tin qua id tuần tự; sinh được ở client; vẫn sắp xếp được theo thời gian |
| Xóa mềm (`deleted_at`) cho dữ liệu người dùng | Sinh viên xóa nhầm dữ liệu học tập của cả một học kỳ là mất mát thật |
| `created_at` / `updated_at` trên mọi bảng | Điều tra sự cố và audit |
| Schema tách theo module (`identity.`, `academic.`…) | Biên giới module hiện diện cả ở tầng database |

### 5.2. Sơ đồ quan hệ lõi (Phase 1)

```
                    ┌──────────┐
                    │  users   │  identity
                    └────┬─────┘
         ┌───────────────┼──────────────┬────────────────┐
         ▼               ▼              ▼                ▼
   ┌───────────┐   ┌──────────┐   ┌──────────┐   ┌──────────────┐
   │enrollments│   │  goals   │   │user_     │   │progress_     │
   │ (academic)│   │  (goal)  │   │skills    │   │snapshots     │
   └─────┬─────┘   └────┬─────┘   │ (skill)  │   │ (progress)   │
         │              │         └────┬─────┘   └──────────────┘
         ▼              ▼              ▼
   ┌───────────┐   ┌──────────┐   ┌──────────┐
   │  courses  │   │ subgoals │   │  skills  │
   └─────┬─────┘   └──────────┘   └────┬─────┘
         │                             │
         ▼                             ▼
   ┌───────────┐               ┌──────────────┐
   │ curricula │               │   roadmaps   │
   └─────┬─────┘               └──────┬───────┘
         │                            │
         └────────────┬───────────────┘
                      ▼
              ┌──────────────┐
              │   sources    │  knowledge
              │ (provenance) │  ← curricula, skills, roadmaps đều trỏ tới đây
              └──────────────┘
```

`sources` nằm ở trung tâm là biểu hiện kiến trúc của giá trị *Trust*: **không có tri thức nào
trong hệ thống mà không có nguồn.**

### 5.3. Dữ liệu thật của người dùng

Dữ liệu học tập là dữ liệu cá nhân. Nó **không bao giờ** được đưa vào repo, kể cả dưới dạng seed
data hay file test. `database/seeds/` chỉ chứa dữ liệu giả (synthetic). `.gitignore` đã loại trừ
`database/seeds/real/`.

---

## 6. Công nghệ và lý do lựa chọn

Chi tiết đánh đổi trong [ADR-0002](adr/0002-backend-tech-stack.md) và
[ADR-0003](adr/0003-postgresql-as-primary-datastore.md).

| Thành phần | Lựa chọn | Lý do chính |
| --- | --- | --- |
| Ngôn ngữ backend | **Java 21 (LTS)** | Kiểu tĩnh giúp refactor an toàn trên dự án dài hạn. *[Inference]* Java/Spring có nhu cầu tuyển dụng lớn ở Việt Nam, đặc biệt khối doanh nghiệp và ngân hàng — nhận định dựa trên quan sát thị trường, không phải số liệu được kiểm chứng trong tài liệu này. |
| Framework | **Spring Boot 3.x** | Hệ sinh thái đầy đủ (security, data, validation, actuator); tài liệu và cộng đồng lớn; buộc phải hiểu DI, transaction, AOP — những khái niệm chuyển được sang mọi backend stack. |
| Build | **Maven** | Cấu hình khai báo, dễ đọc lại sau nhiều tháng; đã có sẵn trên máy dev (3.9.11). |
| Database | **PostgreSQL 16** | Quan hệ dày, cần transaction thật; hỗ trợ `jsonb` cho phần dữ liệu linh hoạt mà không cần thêm database thứ hai. |
| Migration | **Flyway** | SQL thuần, version rõ, học được SQL thật thay vì bị ORM che đi. |
| Truy cập dữ liệu | **Spring Data JPA** + JdbcTemplate/jOOQ cho truy vấn phức tạp | JPA cho CRUD; truy vấn báo cáo phức tạp viết SQL trực tiếp thay vì bẻ cong JPQL. |
| Cache / rate limit | **Redis 7** | Thêm **khi có nhu cầu thật** (nguyên tắc §1.4), không mặc định từ đầu. |
| Auth | **Spring Security + JWT** | Access token ngắn hạn + refresh rotation; bắt buộc hiểu authn/authz thay vì dùng dịch vụ đóng gói. |
| API contract | **OpenAPI 3** (springdoc) | Hợp đồng rõ giữa backend và frontend; sinh client type-safe cho frontend. |
| Test | **JUnit 5 · AssertJ · Testcontainers · ArchUnit** | Domain test không cần hạ tầng; integration test trên PostgreSQL thật; ArchUnit canh biên giới module. |
| Frontend | **Next.js 16 · React 19 · TypeScript · Tailwind 4** | Đủ tốt để chứng minh sản phẩm chạy thật; TypeScript dùng chung tư duy kiểu với backend. Giữ mỏng có chủ ý. |
| Dev infra | **Docker Compose** | Một lệnh dựng được PostgreSQL + Redis; môi trường dev giống nhau ở mọi máy. |
| CI | **GitHub Actions** | Chạy build + test trên mỗi PR; miễn phí cho repo công khai. |
| Deploy (Phase 1) | Container trên **một VPS** | Đủ cho quy mô Phase 1. Kubernetes là độ phức tạp không có người dùng nào cần lúc này. |

### 6.1. Những công nghệ **không** dùng ở Phase 1, và vì sao

Ghi lại để tránh phải tranh luận lại, và để biết điều kiện nào thì đổi ý:

| Không dùng | Vì sao | Điều kiện xem xét lại |
| --- | --- | --- |
| Microservices | Chưa hiểu đủ rõ domain để chia biên giới đúng | Một module cần scale hoặc cần ngôn ngữ khác |
| Kubernetes | Chi phí vận hành vượt xa giá trị ở quy mô hiện tại | Cần nhiều node, nhiều môi trường, autoscaling thật |
| Kafka / RabbitMQ | Event in-process đã đủ | Cần xử lý bất đồng bộ xuyên tiến trình, cần replay event |
| GraphQL | REST + OpenAPI đủ cho một client | Nhiều client với nhu cầu dữ liệu khác nhau rõ rệt |
| MongoDB / NoSQL | Dữ liệu có quan hệ dày, cần transaction | Có khối dữ liệu thật sự không quan hệ và lớn |
| Vector database | Chưa có tính năng AI nào | Phase 2, khi assistant cần semantic search |

---

## 7. Non-functional targets

> Đây là **mục tiêu** để thiết kế và đo lường về sau, **không phải kết quả đo**. Chưa có hệ thống
> nào được benchmark tại thời điểm viết tài liệu.

| Nhóm | Mục tiêu Phase 1 |
| --- | --- |
| Latency đọc (p95) | < 300 ms cho API đọc thông thường |
| Latency ghi (p95) | < 500 ms |
| Dashboard | Nội dung chính hiển thị < 1.5 s trên mạng 4G |
| Truy vấn N+1 | Không có trên các luồng chính — kiểm bằng test đếm số truy vấn |
| Uptime | Mục tiêu vận hành ở mức phù hợp dự án cá nhân; không cam kết SLA |
| Test | Domain logic có unit test; mọi luồng MUST có integration test |
| Bảo mật | Không có secret trong repo; input validate ở biên; truy vấn tham số hóa; kiểm quyền ở tầng service |

**Chỉ số sản phẩm cho *Speed*** không phải latency, mà là *time-to-answer*: thời gian từ khi
sinh viên có câu hỏi đến khi có câu trả lời dùng được. Một API 50 ms nằm sau 6 lần click là
sản phẩm chậm.

---

## 8. Lộ trình tiến hóa kiến trúc

```
Phase 1  Modular monolith · PostgreSQL · Docker Compose · 1 VPS
            │
            │  thêm khi có nhu cầu thật:
Phase 2  + Redis cache · + recommendation module · + AI provider (retrieval-based)
            │  quy tắc: dữ liệu gửi sang AI provider là tối thiểu cần thiết,
            │  và người dùng biết dữ liệu nào được gửi
            ▼
Phase 3  + community module · + hàng đợi cho luồng review · + read replica nếu cần
            │
            ▼
Phase 4  + career module · + tách service đầu tiên NẾU có lý do thật
            (ứng viên khả năng cao nhất: ai-assistant — khác ngôn ngữ, khác nhịp scale)
```

**Nguyên tắc xuyên suốt:** mỗi thành phần hạ tầng mới phải trả lời được câu *"vấn đề thật nào
đang xảy ra mà thành phần này giải quyết?"*. Không trả lời được thì không thêm.

---

## 9. Tài liệu quyết định (ADR)

| ADR | Nội dung |
| --- | --- |
| [0001](adr/0001-modular-monolith.md) | Chọn modular monolith thay vì microservices |
| [0002](adr/0002-backend-tech-stack.md) | Chọn Java + Spring Boot cho backend |
| [0003](adr/0003-postgresql-as-primary-datastore.md) | PostgreSQL là source of truth duy nhất |

Mọi quyết định kiến trúc có ảnh hưởng dài hạn phải có ADR — kể cả quyết định **không làm** một việc.
