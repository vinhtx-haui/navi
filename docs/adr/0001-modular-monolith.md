# ADR-0001: Chọn modular monolith thay vì microservices

- **Trạng thái:** Accepted
- **Ngày:** 2026-08-04
- **Liên quan:** [ADR-0002](0002-backend-tech-stack.md) · [architecture.md](../architecture.md)

## Context — Bối cảnh

Navi là dự án dài hạn, dự kiến trải qua bốn phase với phạm vi mở rộng đáng kể (AI assistant,
community, career). Cần chọn kiểu kiến trúc backend cho Phase 1.

Ràng buộc thực tế tại thời điểm quyết định:

1. **Một người phát triển**, làm song song với việc học đại học.
2. **Domain chưa được hiểu đầy đủ.** Đây là ràng buộc quan trọng nhất — biên giới giữa
   "academic", "progress", "skill" hiện đang là phán đoán, chưa được kiểm chứng bằng code chạy thật.
3. **Chưa có người dùng.** Không có dữ liệu về tải, về điểm nghẽn, về phần nào cần scale.
4. **Mục tiêu kép:** sản phẩm dùng được **và** portfolio thể hiện năng lực backend.
5. Dữ liệu có tính quan hệ dày và cần toàn vẹn (tính GPA, tính tiến độ tín chỉ phải nhất quán).

## Decision — Quyết định

Xây **một Spring Boot application duy nhất**, chia thành **các module theo bounded context**,
với ba ràng buộc bắt buộc:

1. **Mỗi module sở hữu dữ liệu của nó.** Chỉ module sở hữu được ghi vào bảng thuộc về nó.
2. **Module chỉ giao tiếp qua interface công khai** (`XModuleApi`), không import trực tiếp
   package `domain` của module khác.
3. **Các ràng buộc trên được kiểm bằng test kiến trúc** (ArchUnit), không chỉ bằng quy ước trong tài liệu.

Domain logic viết bằng Java thuần, không phụ thuộc Spring hay JPA, để test được độc lập và để
việc di chuyển module về sau không kéo theo framework.

## Alternatives considered — Phương án đã cân nhắc

### A. Microservices ngay từ đầu

**Điểm mạnh:** scale độc lập; deploy độc lập; giá trị học tập về distributed systems; *[Inference]*
thường được xem là "kiến trúc hiện đại" trong tuyển dụng.

**Vì sao không chọn:**

- Chia biên giới khi chưa hiểu domain là rủi ro lớn nhất. Sửa biên giới sai trong monolith là
  refactor; sửa biên giới sai giữa các service là viết lại nhiều service cộng với hợp đồng API
  giữa chúng.
- Mất transaction ACID xuyên module. Nghiệp vụ như "hoàn thành môn → cập nhật tiến độ → kiểm mục
  tiêu" sẽ cần saga hoặc eventual consistency **ngay từ đầu**, cho một vấn đề chưa tồn tại.
- Chi phí vận hành (service discovery, distributed tracing, orchestration, nhiều pipeline) do một
  người gánh, cho một hệ thống chưa có người dùng.
- Giá trị học tập bị đặt sai thứ tự: học distributed systems trên nền domain design chưa vững sẽ
  tạo ra một hệ thống phức tạp mà không giải quyết vấn đề thật.

### B. Monolith không phân module (layered thuần: controller / service / repository toàn cục)

**Điểm mạnh:** đơn giản nhất, đi nhanh nhất ở tuần đầu.

**Vì sao không chọn:** với dự án dài nhiều năm, phân tầng ngang mà không có biên giới dọc thường
dẫn tới `service` gọi chéo tự do và mọi thứ phụ thuộc lẫn nhau. Khi đó vừa không tách được service
về sau, vừa khó test từng phần. Cái giá trả sau lớn hơn nhiều lợi ích ban đầu.

### C. Serverless (functions)

**Vì sao không chọn:** cold start ảnh hưởng trực tiếp giá trị *Speed*; quản lý kết nối database
từ function phức tạp; domain có state và quan hệ dày, không phù hợp mô hình function rời rạc;
khó chạy toàn hệ thống trên máy local — làm chậm vòng lặp phát triển của một dev đơn lẻ.

## Consequences — Hệ quả

### Tích cực

- Refactor biên giới module là thao tác trong một codebase, có compiler hỗ trợ.
- Transaction ACID trong một database — dữ liệu tiến độ luôn nhất quán mà không cần saga.
- Một lệnh để chạy toàn bộ hệ thống ở local; vòng lặp phát triển ngắn.
- Vẫn học được phần khó và bền nhất của backend: domain modeling, biên transaction, thiết kế
  schema, tối ưu truy vấn, bảo mật, test.
- Đường tách service về sau đã được mở sẵn: chỉ cần thay implementation của `XModuleApi` bằng
  HTTP client, domain logic không đổi.

### Tiêu cực / cái giá phải trả

- **Kỷ luật thay cho ép buộc kỹ thuật.** Không có ranh giới mạng nào ngăn việc gọi tắt vào bảng
  của module khác — đây là điểm yếu thật của phương án này. Giảm thiểu bằng ArchUnit test và
  schema tách theo module.
- Không scale được từng phần độc lập. Chấp nhận được ở quy mô Phase 1.
- Một lỗi nặng ảnh hưởng toàn ứng dụng. Giảm thiểu bằng xử lý lỗi ở biên module và health check.
- *[Inference]* Có thể bị đánh giá là "kiến trúc đơn giản" nếu người xem portfolio chỉ nhìn nhãn
  công nghệ. Giảm thiểu bằng cách để ADR này trong repo — quyết định có lý do rõ ràng thường thể
  hiện năng lực tốt hơn việc chọn công nghệ phức tạp không có lý do.

### Rủi ro và cách giảm thiểu

| Rủi ro | Giảm thiểu |
| --- | --- |
| Module xói mòn biên giới theo thời gian | ArchUnit test chạy trong CI; vi phạm làm build đỏ |
| Truy vấn chéo module vì "nhanh hơn" | Schema tách theo module; review chính mình theo checklist trước khi merge |
| Ứng dụng phình to khó khởi động | Theo dõi thời gian khởi động; đặt ngưỡng cảnh báo |

## When to revisit — Điều kiện xem xét lại

Xem xét tách service khi có **ít nhất một** dấu hiệu **đo được** (không phải cảm giác):

1. Một module cần scale khác hẳn phần còn lại (ví dụ ai-assistant chiếm phần lớn tài nguyên).
2. Một module cần công nghệ khác (ví dụ Python cho ML) và không thể giải quyết bằng cách gọi
   dịch vụ ngoài.
3. Một module có nhịp thay đổi/deploy khác hẳn, và việc deploy chung trở thành điểm nghẽn thật.
4. Thời gian build/test vượt ngưỡng làm chậm vòng lặp phát triển một cách rõ rệt.

Không tách service chỉ vì "đã đến lúc" hoặc vì lý do trình diễn công nghệ.
