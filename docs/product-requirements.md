# Product Requirements Document (PRD)

- **Sản phẩm:** Navi Platform
- **Phạm vi tài liệu:** Phase 1 — Foundation (chi tiết) · Phase 2–4 (định hướng)
- **Trạng thái:** Draft v1 · 2026-08-04
- **Tài liệu liên quan:** [vision.md](vision.md) · [roadmap.md](roadmap.md) · [architecture.md](architecture.md)

---

## 1. Problem Statement

### 1.1. Phát biểu vấn đề

> Sinh viên đại học phải tự ra những quyết định quan trọng về học tập và nghề nghiệp
> **mà không có bức tranh đầy đủ về chính mình, không có dữ liệu đáng tin cậy, và không có
> phản hồi kịp thời về tiến độ.** Kết quả là họ ra quyết định bằng cảm giác và thông tin
> truyền miệng, và thường chỉ phát hiện mình đi sai hướng khi đã tốn hàng tháng.

### 1.2. Vấn đề được chia thành các vấn đề con

| Mã | Vấn đề con | Ảnh hưởng | Ưu tiên |
| --- | --- | --- | --- |
| **P1** | Dữ liệu học tập phân mảnh trên nhiều nơi (portal trường, chat, note, trí nhớ) | Không tự đánh giá được mình đang ở đâu | Cao |
| **P2** | Không đo được tiến độ so với chương trình đào tạo và so với mục tiêu cá nhân | Không biết mình chậm hay đúng hạn | Cao |
| **P3** | Mục tiêu dài hạn quá lớn, không chia được thành bước hành động | Trì hoãn, mất động lực | Cao |
| **P4** | Lộ trình kỹ năng trên Internet chung chung, không kiểm chứng, không khớp chương trình học | Học sai thứ tự, học thứ đã lỗi thời | Cao |
| **P5** | Không có phản hồi giữa kỳ, chỉ có điểm cuối kỳ | Phát hiện vấn đề quá muộn | Trung bình |
| **P6** | Không rõ khoảng cách giữa năng lực hiện tại và yêu cầu công việc | Ra trường thiếu chuẩn bị | Trung bình (Phase 4) |

### 1.3. Vì sao các giải pháp hiện có chưa đủ

| Giải pháp hiện có | Thiếu gì |
| --- | --- |
| Portal / LMS của trường | Chỉ có dữ liệu hành chính. Không có mục tiêu cá nhân, không có kỹ năng, không định hướng. |
| Notion / Excel tự làm | Linh hoạt nhưng không có tri thức domain — không biết môn tiên quyết, không biết thứ tự kỹ năng hợp lý; tự bảo trì tốn công. |
| Roadmap.sh và tương tự | Lộ trình chất lượng nhưng **không cá nhân hóa**: không biết người dùng đã học gì, đang ở đâu, chương trình trường ra sao. |
| App to-do / habit tracker | Quản lý được task, nhưng không hiểu ngữ cảnh học thuật và không định hướng. |
| ChatGPT và trợ lý AI chung | Trả lời nhanh nhưng **không có dữ liệu của người dùng**, và có thể bịa thông tin — vi phạm trực tiếp giá trị *Trust*. |

**Khoảng trống Navi nhắm vào:** *cá nhân hóa dựa trên dữ liệu thật của người dùng* **giao với**
*tri thức domain có kiểm chứng*. Không sản phẩm nào ở trên có cả hai.

---

## 2. User Personas

### Persona 1 — Minh, sinh viên năm 2 CNTT · "Người mất phương hướng"

| | |
| --- | --- |
| **Bối cảnh** | Năm 2, học lực trung bình khá. Biết mình muốn làm lập trình, chưa biết hướng nào. |
| **Hành vi hiện tại** | Bookmark 15 roadmap, bắt đầu 4 khóa online, hoàn thành 0. Điểm ghi trong đầu. |
| **Mục tiêu** | Có một hướng rõ ràng và biết việc cần làm **tuần này**. |
| **Nỗi đau** | *"Em thấy cái gì cũng cần học, không biết bắt đầu từ đâu, học mãi vẫn thấy mình chưa biết gì."* |
| **Navi giúp gì** | Chia mục tiêu lớn thành bước nhỏ; một lộ trình duy nhất thay cho 15 bookmark; thấy được tiến bộ để giữ động lực. |
| **Thành công với Minh** | Sau 1 tháng, Minh hoàn thành được các bước đầu của **một** lộ trình, thay vì bỏ dở nhiều lộ trình. |

### Persona 2 — Hà, sinh viên năm 4 CNTT · "Người chạy nước rút"

| | |
| --- | --- |
| **Bối cảnh** | Năm 4, GPA tốt, muốn làm backend. Còn 2 học kỳ. |
| **Hành vi hiện tại** | Tự quản lý bằng Excel + Notion. Đọc job description để đoán mình thiếu gì. |
| **Mục tiêu** | Biết chính xác mình còn thiếu gì so với vị trí Backend Fresher, và có bằng chứng năng lực. |
| **Nỗi đau** | *"Em có điểm cao nhưng không biết mình có đủ năng lực thật để đi làm không."* |
| **Navi giúp gì** | Đo khoảng cách kỹ năng theo vai trò cụ thể; hồ sơ năng lực dựa trên dấu vết thật; ưu tiên việc còn lại theo thời gian còn lại. |
| **Thành công với Hà** | Hà chỉ ra được 3 kỹ năng cần bổ sung, có nguồn rõ ràng, và hoàn thành trước khi ứng tuyển. |

### Persona 3 — Anh, sinh viên chuyển hướng · "Người bắt đầu lại"

| | |
| --- | --- |
| **Bối cảnh** | Năm 3, từng theo hướng khác, mới quyết định chuyển sang backend. |
| **Hành vi hiện tại** | Lo mình bắt đầu quá muộn; không biết những gì đã học có dùng được không. |
| **Mục tiêu** | Một lộ trình **tính đến** những gì đã có, không phải bắt đầu từ số 0. |
| **Nỗi đau** | *"Em sợ mình muộn hơn các bạn và không biết cái gì học rồi thì bỏ qua được."* |
| **Navi giúp gì** | Lộ trình trừ đi phần đã hoàn thành; thấy rõ thời gian còn lại và cách sắp xếp thực tế. |
| **Thành công với Anh** | Anh có một lộ trình khả thi trong thời gian còn lại và không học lại thứ mình đã biết. |

### Persona phụ (Phase 3–4)

- **Long, đã tốt nghiệp / đang đi làm** — tham gia để kiểm chứng tri thức và mentor. Động lực:
  đóng góp lại, xây uy tín cá nhân. Chi phí phải cực thấp: đóng góp trong vài phút, không phải vài giờ.

---

## 3. Main Use Cases

### 3.1. Use case chính (Phase 1)

| Mã | Use case | Actor | Kết quả mong đợi |
| --- | --- | --- | --- |
| **UC-01** | Thiết lập hồ sơ học tập ban đầu | Sinh viên | Có ngành, khóa, chương trình đào tạo tham chiếu |
| **UC-02** | Nhập & quản lý môn học | Sinh viên | Danh sách môn theo học kỳ, kèm tín chỉ và điểm |
| **UC-03** | Xem tiến độ học tập | Sinh viên | Biết % tín chỉ hoàn thành, GPA theo thời gian, phần còn thiếu |
| **UC-04** | Tạo và theo dõi mục tiêu | Sinh viên | Mục tiêu lớn được chia thành mục tiêu con có trạng thái |
| **UC-05** | Xây dựng roadmap kỹ năng | Sinh viên | Lộ trình kỹ năng có thứ tự, đánh dấu được mức thành thạo |
| **UC-06** | Xem dashboard tổng quan | Sinh viên | Trả lời được *"tôi đang ở đâu"* trong một màn hình |

### 3.2. Luồng chi tiết: UC-03 — Xem tiến độ học tập

```
Sinh viên mở Dashboard
   │
   ├─▶ Navi đọc: môn đã hoàn thành, tín chỉ, chương trình đào tạo tham chiếu
   │
   ├─▶ Navi tính: % tín chỉ hoàn thành · GPA hiện tại & xu hướng · nhóm môn còn thiếu
   │
   ├─▶ Navi hiển thị kèm nguồn:
   │      "Dựa trên 24 môn bạn đã nhập + chương trình đào tạo K19 SE (verified, cập nhật 2026-07)"
   │
   └─▶ Nếu thiếu dữ liệu (chưa có chương trình đào tạo của trường):
          Navi hiển thị rõ: "Chưa có chương trình đào tạo cho ngành này — tiến độ tín chỉ
          chưa tính được." KHÔNG hiển thị số phỏng đoán.
```

Nhánh cuối là hiện thực hóa giá trị *Trust*: **thiếu dữ liệu thì nói thiếu, không đoán.**

### 3.3. Luồng chi tiết: UC-04 — Tạo và theo dõi mục tiêu

```
Sinh viên tạo mục tiêu: "Trở thành Backend Developer"
   │
   ├─▶ Navi hỏi: mốc thời gian mong muốn · mức hiện tại
   │
   ├─▶ Phase 1: sinh viên tự chia mục tiêu con, hoặc chọn template đã kiểm chứng
   │   Phase 2: Navi tự đề xuất mục tiêu con + thứ tự, kèm giải thích
   │
   ├─▶ Mỗi mục tiêu con: trạng thái (not started / in progress / done), liên kết tới kỹ năng
   │
   └─▶ Tiến độ mục tiêu tính từ mục tiêu con đã hoàn thành, không phải người dùng tự khai %
```

### 3.4. Use case định hướng cho các phase sau

| Phase | Use case |
| --- | --- |
| 2 | Nhận gợi ý bước tiếp theo kèm lý do · phát hiện khoảng cách kỹ năng · hỏi AI assistant về tình hình học tập của mình |
| 3 | Fork roadmap của người đi trước · đóng góp tri thức · kiểm chứng nội dung cộng đồng |
| 4 | Đối chiếu bản thân với một vai trò nghề · sinh portfolio · kết nối mentor |

---

## 4. Functional Requirements — Phase 1

Ký hiệu: **MUST** = bắt buộc cho Phase 1 · **SHOULD** = nên có · **COULD** = nếu còn thời gian.

### 4.1. FR-A — Account & Identity

| ID | Yêu cầu | Mức |
| --- | --- | --- |
| FR-A1 | Đăng ký bằng email + mật khẩu | MUST |
| FR-A2 | Đăng nhập, trả về access token (ngắn hạn) + refresh token (dài hạn, có rotation) | MUST |
| FR-A3 | Mật khẩu được hash bằng thuật toán băm mật khẩu hiện đại có salt (bcrypt/argon2) | MUST |
| FR-A4 | Đăng xuất và thu hồi refresh token | MUST |
| FR-A5 | Người dùng chỉ truy cập được dữ liệu của chính mình (kiểm tra quyền ở tầng service) | MUST |
| FR-A6 | Xác thực email | SHOULD |
| FR-A7 | Đặt lại mật khẩu qua email | SHOULD |
| FR-A8 | Đăng nhập bằng Google | COULD |

### 4.2. FR-B — Academic Profile

| ID | Yêu cầu | Mức |
| --- | --- | --- |
| FR-B1 | Khai báo trường, ngành, khóa, học kỳ hiện tại | MUST |
| FR-B2 | Tham chiếu tới một chương trình đào tạo (curriculum) nếu có trong hệ thống | MUST |
| FR-B3 | Curriculum trong hệ thống **bắt buộc** có `source` và `verification_status` | MUST |
| FR-B4 | Khi chưa có curriculum phù hợp, hệ thống nói rõ và **không** suy đoán tiến độ tín chỉ | MUST |
| FR-B5 | Người dùng đề xuất curriculum mới (vào trạng thái `unverified`) | COULD |

### 4.3. FR-C — Course Management

| ID | Yêu cầu | Mức |
| --- | --- | --- |
| FR-C1 | CRUD môn học: mã môn, tên, số tín chỉ, học kỳ, trạng thái (planned/in-progress/completed/dropped) | MUST |
| FR-C2 | Nhập điểm theo thang điểm của trường; hệ thống lưu cả thang gốc | MUST |
| FR-C3 | Khai báo môn tiên quyết; cảnh báo khi lên kế hoạch học môn chưa đủ tiên quyết | SHOULD |
| FR-C4 | Nhóm môn theo học kỳ và xem theo timeline | MUST |
| FR-C5 | Nhập nhiều môn cùng lúc (bulk import CSV) | COULD |

### 4.4. FR-D — Progress Tracking

| ID | Yêu cầu | Mức |
| --- | --- | --- |
| FR-D1 | Tính tổng tín chỉ đã hoàn thành / tổng tín chỉ yêu cầu | MUST |
| FR-D2 | Tính GPA hiện tại và GPA theo từng học kỳ | MUST |
| FR-D3 | Hiển thị xu hướng GPA qua các học kỳ | SHOULD |
| FR-D4 | Liệt kê nhóm môn còn thiếu so với curriculum | MUST |
| FR-D5 | Mọi số liệu tính toán phải chỉ ra được nó được tính từ dữ liệu nào | MUST |
| FR-D6 | Dự báo thời điểm tốt nghiệp theo tốc độ hiện tại — kèm nhãn rõ đây là **dự báo**, không phải cam kết | COULD |

### 4.5. FR-E — Goal Management

| ID | Yêu cầu | Mức |
| --- | --- | --- |
| FR-E1 | CRUD mục tiêu: tiêu đề, mô tả, mốc thời gian, trạng thái | MUST |
| FR-E2 | Mục tiêu có mục tiêu con (một cấp lồng nhau ở Phase 1) | MUST |
| FR-E3 | Tiến độ mục tiêu **được tính** từ mục tiêu con hoàn thành, không do người dùng tự khai | MUST |
| FR-E4 | Liên kết mục tiêu với môn học hoặc kỹ năng liên quan | SHOULD |
| FR-E5 | Nhắc nhở mục tiêu sắp đến hạn | COULD |

### 4.6. FR-F — Skill Roadmap

| ID | Yêu cầu | Mức |
| --- | --- | --- |
| FR-F1 | Tạo roadmap gồm các bước có thứ tự, mỗi bước gắn với một kỹ năng | MUST |
| FR-F2 | Chọn roadmap từ template đã kiểm chứng (`verified`) | MUST |
| FR-F3 | Đánh dấu mức thành thạo kỹ năng theo thang xác định (xem [glossary.md](glossary.md)) | MUST |
| FR-F4 | Kỹ năng có quan hệ phụ thuộc (kỹ năng A nên học trước B) | SHOULD |
| FR-F5 | Mỗi bước roadmap có thể gắn tài nguyên học tập, kèm nguồn | SHOULD |
| FR-F6 | Mô hình kỹ năng **không** gắn cứng vào ngành CNTT — để mở rộng ngành khác về sau | MUST |

### 4.7. FR-G — Dashboard

| ID | Yêu cầu | Mức |
| --- | --- | --- |
| FR-G1 | Một màn hình trả lời: đang ở đâu · đang làm gì · cần làm gì tiếp | MUST |
| FR-G2 | Hiển thị trạng thái dữ liệu — phần nào đã kiểm chứng, phần nào chưa | MUST |
| FR-G3 | Người dùng mới thấy hướng dẫn onboarding thay vì dashboard trống | SHOULD |

---

## 5. Non-Functional Requirements

| Nhóm | Yêu cầu |
| --- | --- |
| **Trust** | Mọi entity tri thức có `source` + `verification_status`. Không có giá trị mặc định là `verified`. Thiếu dữ liệu → trả trạng thái *unknown*, không đoán. |
| **Speed** | Ngân sách hiệu năng cho API đọc và tải trang được đặt và theo dõi — xem [architecture.md](architecture.md) §7. Chỉ số sản phẩm là *time-to-answer*, không chỉ latency. |
| **Security** | Mật khẩu băm có salt; JWT hết hạn ngắn + refresh rotation; kiểm tra quyền ở tầng service (không chỉ ở API); tham số hóa truy vấn; validate mọi input ở biên. |
| **Privacy** | Dữ liệu học tập là dữ liệu cá nhân. Mặc định kín. Không chia sẻ khi người dùng chưa chủ động bật. Dữ liệu thật của người dùng **không bao giờ** được commit vào repo. |
| **Maintainability** | Module có biên giới rõ; domain logic tách khỏi framework; ADR cho mọi quyết định lớn. |
| **Testability** | Domain logic test được không cần database; integration test chạy trên PostgreSQL thật qua Testcontainers. |
| **Observability** | Structured logging có correlation id; health/readiness endpoint; metrics cho các luồng chính. |

---

## 6. Giả định & rủi ro

| Loại | Nội dung | Cách xử lý |
| --- | --- | --- |
| Giả định | Sinh viên chịu bỏ 10 phút nhập dữ liệu ban đầu | Đo tỉ lệ hoàn thành onboarding; nếu thấp, ưu tiên bulk import (FR-C5) |
| Giả định | Có thể lấy được curriculum chính xác của ít nhất một trường | Bắt đầu với một trường/ngành cụ thể; không hứa hỗ trợ mọi trường |
| Rủi ro | Dữ liệu curriculum thay đổi theo năm, dễ lỗi thời | Curriculum có version + ngày cập nhật; dữ liệu cũ bị đánh dấu là cũ |
| Rủi ro | Phình phạm vi sang AI/social quá sớm | Roadmap ràng buộc "không nhảy phase"; ADR bắt buộc cho thay đổi lớn |
| Rủi ro | Dự án cá nhân dài hạn dễ mất động lực | Phase 1 chia thành lát mỏng có thể ship được; tài liệu giữ được ngữ cảnh khi quay lại sau thời gian nghỉ |

*[Inference]* Các giả định và rủi ro trên là phán đoán dựa trên bối cảnh sản phẩm, chưa được
kiểm chứng bằng dữ liệu người dùng thật của Navi.
