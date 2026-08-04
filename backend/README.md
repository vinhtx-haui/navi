# Navi Backend

Spring Boot modular monolith. Đây là trung tâm của hệ thống — xem
[docs/architecture.md](../docs/architecture.md) trước khi viết code.

**Trạng thái:** chưa khởi tạo. Thư mục này hiện chỉ có tài liệu.

---

## Trước khi khởi tạo — checklist môi trường

Môi trường dev đã kiểm tra ngày 2026-08-04:

| Yêu cầu | Trạng thái trên máy |
| --- | --- |
| JDK 21 (LTS) | ⚠️ **Đang có JDK 17.0.17** — cần cài JDK 21, hoặc hạ mục tiêu xuống Java 17 và cập nhật [ADR-0002](../docs/adr/0002-backend-tech-stack.md) |
| Maven 3.9+ | ✅ 3.9.11 |
| Docker | ✅ 28.5.1 |

Spring Boot 3.x chạy được trên Java 17, nên cả hai hướng đều hợp lệ. Cần **quyết định và ghi lại**
trước khi tạo `pom.xml`, vì đổi phiên bản Java sau khi có code là việc gây ma sát không cần thiết.

## Cấu trúc dự kiến

```
backend/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/navi/
│   │   │   ├── NaviApplication.java
│   │   │   ├── shared/              # shared kernel — giữ càng nhỏ càng tốt
│   │   │   │   ├── domain/          # Credit, Grade, Provenance, VerificationStatus
│   │   │   │   ├── error/           # mô hình lỗi chung
│   │   │   │   └── event/           # domain event bus
│   │   │   ├── identity/            # module: user, auth
│   │   │   ├── academic/            # module: curriculum, course, enrollment
│   │   │   ├── progress/            # module: tính toán tiến độ
│   │   │   ├── goal/                # module: mục tiêu
│   │   │   ├── skill/               # module: kỹ năng, roadmap
│   │   │   └── knowledge/           # module: source, verification
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-dev.yml
│   └── test/
│       └── java/com/navi/
│           ├── architecture/        # ArchUnit — canh biên giới module
│           └── ...                  # unit test theo module + integration test
└── README.md
```

Mỗi module theo bốn lớp `api / application / domain / infrastructure` — chi tiết và **quy tắc phụ
thuộc** trong [docs/architecture.md](../docs/architecture.md) §2.3.

## Quy tắc bắt buộc

Ba quy tắc dưới đây được kiểm bằng ArchUnit test trong CI, không phải bằng quy ước:

1. **`domain` không import Spring, không import JPA.** Domain logic là Java thuần, test được
   không cần context và không cần database.
2. **Module chỉ gọi module khác qua `XModuleApi`.** Không import trực tiếp package `domain` của
   module khác, không truy vấn bảng thuộc module khác.
3. **Không có phụ thuộc vòng giữa các module.**

Ngoài ra:

- **Biên transaction đặt ở tầng `application`** — không ở controller, không ở domain.
- **Không dùng `spring.jpa.hibernate.ddl-auto: update`** ở bất kỳ profile nào. Schema do Flyway
  định nghĩa. Ở test dùng `validate`.
- **Không commit secret.** `application-local.yml` và `.env` đã bị `.gitignore` loại trừ; commit
  file `.example` thay thế.
- **Kiểm quyền ở tầng service**, không chỉ ở controller. Mọi truy vấn dữ liệu người dùng phải
  ràng buộc theo `userId` của người đang đăng nhập.

## Thứ tự khởi tạo đề xuất

Thứ tự này được sắp để mỗi bước tạo ra một thứ chạy được và kiểm chứng được, thay vì dựng toàn bộ
khung rồi mới thử:

1. `pom.xml` + `NaviApplication` + health endpoint chạy được → xác nhận nền tảng ổn.
2. Docker Compose PostgreSQL + kết nối thành công → xác nhận hạ tầng dev ổn.
3. Flyway baseline migration (`V1__baseline.sql`) → xác nhận quy trình migration ổn.
4. ArchUnit test cho ba quy tắc trên → **đặt ràng buộc trước khi có code để ràng buộc**.
5. Module `identity`: register / login / refresh, có integration test.
6. Module `academic`: course + enrollment CRUD.
7. Module `progress`: tính GPA và tiến độ tín chỉ — domain logic thuần, unit test kỹ.
8. Module `goal`, `skill`.

Bước 4 đặt trước bước 5 có chủ ý: ràng buộc kiến trúc rẻ khi thiết lập từ đầu, đắt khi phải áp
lên codebase đã vi phạm.

## Lệnh thường dùng (sau khi khởi tạo)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

```bash
./mvnw test
```

```bash
./mvnw verify
```
