# Architecture Decision Records (ADR)

Mỗi file trong thư mục này ghi lại **một quyết định kiến trúc** cùng với bối cảnh, các phương án
đã cân nhắc, và hệ quả.

## Vì sao cần ADR trên một dự án cá nhân

Đây là dự án dài hạn, phát triển song song với việc học. Sẽ có những khoảng nghỉ nhiều tuần.
ADR trả lời câu hỏi mà chính người viết code sẽ hỏi sau ba tháng: *"tại sao lúc đó mình chọn cái
này?"* — thay vì phải suy diễn lại từ code, hoặc tệ hơn là đổi ngược quyết định mà không biết
lý do ban đầu.

Ngoài ra, ADR làm rõ **điều kiện để đổi ý**. Một quyết định không có điều kiện đổi ý sẽ bị giữ
lại quá lâu, hoặc bị bỏ quá sớm.

## Quy ước

- Tên file: `NNNN-tieu-de-ngan.md` (số tăng dần, không dùng lại số cũ)
- Trạng thái: `Proposed` · `Accepted` · `Deprecated` · `Superseded by ADR-XXXX`
- **Không sửa nội dung ADR đã `Accepted`.** Khi đổi quyết định, viết ADR mới và đánh dấu ADR cũ
  là `Superseded`. Lịch sử quyết định có giá trị hơn một tài liệu luôn "đúng".

## Khi nào cần viết ADR

Viết khi quyết định thỏa **ít nhất một** điều kiện sau:

- Khó đảo ngược (chọn database, chọn kiến trúc, chọn mô hình auth)
- Ảnh hưởng nhiều module hoặc nhiều phase
- Có phương án thay thế hợp lý mà mình đã cân nhắc rồi loại bỏ
- Là quyết định **không làm** một việc (những cái này đặc biệt dễ bị quên lý do)

## Danh sách

| ADR | Tiêu đề | Trạng thái | Ngày |
| --- | --- | --- | --- |
| [0001](0001-modular-monolith.md) | Chọn modular monolith thay vì microservices | Accepted | 2026-08-04 |
| [0002](0002-backend-tech-stack.md) | Java 21 + Spring Boot cho backend | Accepted | 2026-08-04 |
| [0003](0003-postgresql-as-primary-datastore.md) | PostgreSQL là source of truth duy nhất | Accepted | 2026-08-04 |

## Template

```markdown
# ADR-NNNN: <Tiêu đề>

- **Trạng thái:** Proposed | Accepted | Deprecated | Superseded by ADR-XXXX
- **Ngày:** YYYY-MM-DD
- **Liên quan:** ADR-XXXX, docs/...

## Context — Bối cảnh
Vấn đề gì cần quyết định? Ràng buộc nào đang có?

## Decision — Quyết định
Chọn gì. Viết ở thể chủ động, dứt khoát.

## Alternatives considered — Phương án đã cân nhắc
Từng phương án: điểm mạnh, điểm yếu, vì sao không chọn.

## Consequences — Hệ quả
### Tích cực
### Tiêu cực / cái giá phải trả
### Rủi ro và cách giảm thiểu

## When to revisit — Điều kiện xem xét lại
Dấu hiệu cụ thể nào cho biết quyết định này không còn đúng.
```
