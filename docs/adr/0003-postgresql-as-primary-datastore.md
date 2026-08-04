# ADR-0003: PostgreSQL là source of truth duy nhất

- **Trạng thái:** Accepted
- **Ngày:** 2026-08-04
- **Liên quan:** [ADR-0001](0001-modular-monolith.md) · [architecture.md](../architecture.md) §5

## Context — Bối cảnh

Dữ liệu của Navi có ba đặc điểm định hình quyết định này:

1. **Quan hệ dày.** Môn học ↔ môn tiên quyết ↔ chương trình đào tạo ↔ kỹ năng ↔ roadmap ↔ mục
   tiêu. Gần như mọi truy vấn có giá trị đều đi qua nhiều quan hệ.
2. **Cần toàn vẹn.** GPA và tiến độ tín chỉ là con số sinh viên dùng để ra quyết định. Dữ liệu
   không nhất quán ở đây vi phạm trực tiếp giá trị *Trust*.
3. **Có phần cần linh hoạt.** Metadata của roadmap, cấu hình bước học, sau này là ngữ cảnh hội
   thoại AI — những phần này chưa có schema ổn định.

## Decision — Quyết định

**PostgreSQL 16 là source of truth duy nhất.** Mọi dữ liệu nghiệp vụ sống ở đây.

Các quyết định kèm theo:

| Hạng mục | Quyết định |
| --- | --- |
| Schema | Quản lý bằng **Flyway migration** có version, SQL thuần. **Không dùng `ddl-auto: update`** ở bất kỳ môi trường nào |
| Phần dữ liệu linh hoạt | Dùng cột **`jsonb`** trong PostgreSQL, không thêm database thứ hai |
| Khóa chính | **UUID v7** |
| Xóa dữ liệu người dùng | **Xóa mềm** (`deleted_at`) |
| Audit | `created_at`, `updated_at` trên mọi bảng |
| Tổ chức | **Schema tách theo module** (`identity.`, `academic.`, `progress.`…) |
| Test | Integration test chạy trên **PostgreSQL thật** qua Testcontainers, không dùng H2 |
| Redis | Chỉ dùng cho cache / rate limit / phiên. **Không bao giờ** là nơi lưu trữ duy nhất của dữ liệu nghiệp vụ |

## Alternatives considered — Phương án đã cân nhắc

### A. MongoDB (document database)

**Điểm mạnh:** schema linh hoạt, phù hợp giai đoạn domain còn thay đổi; dễ bắt đầu.

**Vì sao không chọn:**

- Dữ liệu Navi **là** dữ liệu quan hệ. Truy vấn kiểu "những môn còn thiếu so với chương trình đào
  tạo, có tính môn tiên quyết" là join tự nhiên trong SQL, nhưng phải xử lý ở tầng ứng dụng khi
  dùng document store.
- Tính toán GPA và tiến độ cần transaction xuyên nhiều thực thể.
- PostgreSQL đã có `jsonb` — đáp ứng nhu cầu linh hoạt mà không mất tính quan hệ. Không cần đánh
  đổi toàn bộ mô hình dữ liệu để lấy linh hoạt ở một phần nhỏ.
- Về học tập: SQL và thiết kế quan hệ là kỹ năng nền tảng và bền cho một backend developer.

### B. PostgreSQL + MongoDB (polyglot persistence ngay từ đầu)

**Vì sao không chọn:** hai database nghĩa là hai mô hình nhất quán, hai chiến lược backup, hai bộ
migration, và bài toán đồng bộ giữa chúng — cho một hệ thống chưa có người dùng. Vi phạm nguyên
tắc *"chọn độ phức tạp muộn nhất có thể"* trong [architecture.md](../architecture.md) §1.

### C. MySQL / MariaDB

**Điểm mạnh:** phổ biến, hosting rẻ, đủ tốt cho nhu cầu Phase 1.

**Vì sao không chọn:** PostgreSQL có `jsonb` với index tốt hơn, CTE recursive (hữu ích cho truy
vấn cây tiên quyết và cây kỹ năng), window function mạnh (hữu ích cho tính xu hướng GPA theo học
kỳ), và hệ thống type phong phú hơn. Với cùng chi phí vận hành, PostgreSQL cho nhiều dư địa hơn ở
các phase sau.

### D. SQLite cho Phase 1, đổi sau

**Vì sao không chọn:** khác biệt hành vi giữa SQLite và PostgreSQL (kiểu dữ liệu, concurrency,
tính năng SQL) sẽ tạo ra chi phí di chuyển và những lỗi chỉ xuất hiện ở production. Docker
Compose làm việc chạy PostgreSQL ở local đủ dễ để không cần đánh đổi này.

## Consequences — Hệ quả

### Tích cực

- Transaction ACID cho các nghiệp vụ tính toán — dữ liệu tiến độ luôn nhất quán.
- Ràng buộc toàn vẹn (foreign key, `NOT NULL`, `CHECK`) được database bảo đảm, không phụ thuộc
  vào việc tầng ứng dụng có nhớ kiểm tra hay không. Đây là cách hiện thực giá trị *Trust* ở tầng
  thấp nhất: `verification_status NOT NULL` không có `DEFAULT 'VERIFIED'` khiến việc "quên ghi
  nguồn" trở thành lỗi ngay lúc ghi, không phải một bản ghi âm thầm sai.
- Flyway cho phép tái tạo schema từ đầu ở bất kỳ môi trường nào; lịch sử schema có thể đọc lại.
- Học được SQL thật, thiết kế index, đọc `EXPLAIN ANALYZE` — kỹ năng nền tảng và bền.
- Một database nghĩa là một chiến lược backup, một mô hình nhất quán, một thứ cần vận hành.

### Tiêu cực / cái giá phải trả

- Thay đổi schema cần viết migration — chậm hơn document store ở giai đoạn thử nghiệm nhanh.
  Đây là đánh đổi có ý thức: chậm hơn khi thay đổi, an toàn hơn khi vận hành.
- Cần chạy PostgreSQL ở local (qua Docker) thay vì dùng file database.
- Scale ghi theo chiều ngang khó hơn NoSQL. Không phải vấn đề ở quy mô Phase 1–3.

### Rủi ro và cách giảm thiểu

| Rủi ro | Giảm thiểu |
| --- | --- |
| Migration sai làm mất dữ liệu | Mọi migration test trên bản sao dữ liệu trước khi chạy production; backup trước migration |
| Lạm dụng `jsonb` khiến schema mất kiểm soát | Chỉ dùng `jsonb` cho dữ liệu **không** cần truy vấn quan hệ; khi một trường trong `jsonb` cần join hoặc index thường xuyên → chuyển thành cột thật |
| N+1 query từ JPA | Test đếm số truy vấn trên các luồng chính; SQL trực tiếp cho truy vấn báo cáo |
| Dữ liệu thật của sinh viên bị commit vào repo | `.gitignore` loại trừ `database/seeds/real/` và các file dump; seed data chỉ chứa dữ liệu giả |

## When to revisit — Điều kiện xem xét lại

- Phase 2 cần semantic search cho AI assistant → xem xét **`pgvector`** (extension của PostgreSQL)
  **trước khi** xem xét một vector database riêng.
- Có bằng chứng đo được rằng một khối dữ liệu cụ thể (ví dụ log hội thoại AI) tăng trưởng theo
  cách không phù hợp với PostgreSQL.
- Truy vấn đọc trở thành điểm nghẽn thật → thêm read replica trước khi đổi database.

Không đổi database vì lý do trình diễn công nghệ. Đổi database là một trong những thay đổi đắt
nhất có thể thực hiện trên một hệ thống đang chạy.
