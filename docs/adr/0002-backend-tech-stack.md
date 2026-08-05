# ADR-0002: Java 21 + Spring Boot cho backend

- **Trạng thái:** Accepted
- **Ngày:** 2026-08-04
- **Liên quan:** [ADR-0001](0001-modular-monolith.md) · [ADR-0003](0003-postgresql-as-primary-datastore.md)

## Context — Bối cảnh

Cần chọn ngôn ngữ và framework cho backend Navi. Tiêu chí, theo thứ tự ưu tiên:

1. **Phù hợp mục tiêu nghề nghiệp** — người phát triển định hướng trở thành Backend Developer.
2. **Bền cho dự án dài hạn** — codebase sẽ được đọc lại sau nhiều tháng nghỉ.
3. **Giá trị học tập** — nên buộc phải hiểu các khái niệm backend nền tảng, không che chúng đi.
4. **Giá trị portfolio.**
5. Đủ khả năng cho các phase sau (AI, community, career).

Môi trường dev hiện tại đã xác nhận: **OpenJDK 17.0.17**, **Maven 3.9.11**, **Node v22.21.1**,
**Docker 28.5.1**.

## Decision — Quyết định

**Java 21 (LTS) + Spring Boot 3.x + Maven.**

Stack đi kèm:

| Thành phần | Lựa chọn |
| --- | --- |
| Truy cập dữ liệu | Spring Data JPA cho CRUD; JdbcTemplate hoặc jOOQ cho truy vấn phức tạp |
| Migration | Flyway (SQL thuần, có version) |
| Bảo mật | Spring Security + JWT (access token ngắn hạn + refresh rotation) |
| API contract | OpenAPI 3 qua springdoc |
| Test | JUnit 5 · AssertJ · Testcontainers · ArchUnit |
| Observability | Spring Boot Actuator + structured logging có correlation id |

> **Trạng thái môi trường (2026-08-05):** đã cài **OpenJDK 21.0.12** qua Homebrew
> (`brew install openjdk@21`). Formula này là *keg-only*, nên nó không tự xuất hiện với
> `/usr/libexec/java_home` và không nằm trên `PATH`; cần trỏ `JAVA_HOME` tới
> `/opt/homebrew/opt/openjdk@21`. Xem [backend/README.md](../../backend/README.md) §Môi trường.

## Alternatives considered — Phương án đã cân nhắc

### A. Node.js + NestJS + TypeScript

**Điểm mạnh:** dùng một ngôn ngữ cho cả frontend và backend; hệ sinh thái npm rộng; khởi động
nhanh; NestJS có cấu trúc module tương tự Spring.

**Vì sao không chọn:**

- *[Inference]* Ở thị trường Việt Nam, vị trí backend Java/Spring — đặc biệt trong khối doanh
  nghiệp, ngân hàng, và các công ty gia công lớn — xuất hiện nhiều và thường có lộ trình rõ ràng
  hơn cho người mới. Đây là nhận định dựa trên quan sát thị trường tuyển dụng, **không phải số
  liệu được kiểm chứng trong tài liệu này**; người đọc nên tự khảo sát tin tuyển dụng thực tế.
- TypeScript có kiểu ở compile-time nhưng mất kiểu ở runtime, cần thêm lớp validate riêng.
- Hệ sinh thái Node thay đổi nhanh; với dự án nhiều năm có những khoảng nghỉ dài, điều này tạo
  chi phí bảo trì.
- Ưu điểm "một ngôn ngữ cho cả hai đầu" ít giá trị ở đây vì frontend được giữ mỏng có chủ ý.

### B. .NET 8 + ASP.NET Core

**Điểm mạnh:** framework chất lượng cao, Entity Framework Core tốt, hiệu năng mạnh; *[Inference]*
cũng có nhu cầu tuyển dụng đáng kể tại Việt Nam.

**Vì sao không chọn:** *[Inference]* Hệ sinh thái tài liệu và cộng đồng cho người học ở Việt Nam
mà tôi quan sát được nghiêng về Java/Spring nhiều hơn; đây là phán đoán, không phải sự thật đo
được. Ngoài ra kinh nghiệm sẵn có với JVM giúp giảm ma sát ban đầu. Đây là lựa chọn hợp lý thứ hai
chứ không phải lựa chọn sai.

### C. Go + Gin/Echo

**Điểm mạnh:** đơn giản, biên dịch nhanh, binary gọn, concurrency tốt.

**Vì sao không chọn:** ít cấu trúc mặc định nên phải tự dựng nhiều thứ (validation, DI, migration,
security) — điều này làm chậm tiến độ sản phẩm ở giai đoạn đầu. Về học tập, Go dạy tốt về
concurrency nhưng ít buộc phải hiểu các khái niệm mà Navi cần trước (transaction, ORM, domain
modeling phức tạp).

### D. Python + FastAPI

**Điểm mạnh:** viết nhanh, dễ đọc, thuận lợi nhất cho tính năng AI ở Phase 2.

**Vì sao không chọn:** kiểu động làm refactor trên codebase lớn rủi ro hơn — bất lợi trực tiếp
cho dự án nhiều năm. Nhu cầu về AI ở Phase 2 giải quyết được bằng cách gọi API của nhà cung cấp
model, hoặc bằng một service Python riêng nếu về sau thật sự cần training — không cần đánh đổi
toàn bộ backend cho nhu cầu chưa tồn tại.

## Consequences — Hệ quả

### Tích cực

- Kiểu tĩnh và compiler hỗ trợ refactor — quan trọng khi domain còn thay đổi.
- Spring buộc phải hiểu dependency injection, transaction boundary, AOP, filter chain — những
  khái niệm chuyển được sang mọi backend stack khác.
- Testcontainers cho phép integration test trên PostgreSQL thật, không phải H2 — test phản ánh
  đúng hành vi production.
- Hệ sinh thái ổn định, ít breaking change giữa các bản LTS; dự án nghỉ vài tháng vẫn build được.
- Spring Boot Actuator cung cấp health check và metrics sẵn — hạ thấp chi phí observability.

### Tiêu cực / cái giá phải trả

- **Verbose hơn** Node/Python: cùng một tính năng cần nhiều dòng hơn.
- **Học ban đầu dốc:** Spring có nhiều "magic" (auto-configuration, proxy). Giảm thiểu bằng cách
  giữ domain logic là Java thuần, để phần magic chỉ nằm ở tầng ngoài.
- **Khởi động chậm hơn** và tốn RAM hơn — ảnh hưởng chi phí VPS. Chấp nhận được ở quy mô hiện tại.
- Nếu về sau muốn làm ML training thật, sẽ cần thêm một service Python — chấp nhận đa ngôn ngữ
  từ Phase 2 trở đi.

### Rủi ro và cách giảm thiểu

| Rủi ro | Giảm thiểu |
| --- | --- |
| Lạm dụng "Spring magic", không hiểu bên dưới | Domain layer cấm import Spring; ArchUnit kiểm |
| JPA sinh truy vấn kém hiệu quả (N+1) | Test đếm số truy vấn trên luồng chính; dùng SQL trực tiếp cho truy vấn phức tạp |
| Máy dev đang có Java 17, quyết định ghi Java 21 | Kiểm tra và cài JDK 21 trước khi khởi tạo backend, hoặc hạ mục tiêu xuống 17 và cập nhật ADR này |

## When to revisit — Điều kiện xem xét lại

- Có bằng chứng cụ thể (không phải cảm giác) rằng chi phí tài nguyên JVM là vấn đề thật cho ngân
  sách hạ tầng.
- Một module cần đặc tính mà JVM không phù hợp — khi đó tách **module đó** sang ngôn ngữ khác,
  không đổi toàn bộ stack.
- Định hướng nghề nghiệp của người phát triển thay đổi. Đây là lý do hợp lệ và quan trọng: stack
  được chọn phần lớn vì mục tiêu nghề nghiệp, nên mục tiêu đổi thì quyết định nên được xem lại.
