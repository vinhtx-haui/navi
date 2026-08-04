# Vision — Tầm nhìn

> **Navi** — *A personal growth navigation system that helps students understand where they
> are, where they should go next, and why.*

Tài liệu này trả lời câu hỏi **tại sao Navi tồn tại**. Nó là tài liệu ổn định nhất trong repo:
tính năng sẽ thay đổi, kiến trúc sẽ thay đổi, nhưng lý do tồn tại thì không nên thay đổi tùy tiện.

- **Trạng thái:** Draft v1
- **Ngày viết:** 2026-08-04
- **Người viết:** Navi core team

---

## 1. Navi tồn tại để giải quyết vấn đề gì?

### 1.1. Vấn đề gốc

Sinh viên đại học ngày nay **không thiếu thông tin — họ thiếu định hướng**.

Một sinh viên CNTT năm 2 có thể tìm được hàng trăm roadmap trên Internet trong 10 phút. Nhưng
sau 10 phút đó, những câu hỏi thật sự quan trọng vẫn không được trả lời:

- Học kỳ này tôi nên ưu tiên môn nào, học cái gì trước?
- Tôi đang đi nhanh hay chậm so với mục tiêu của chính tôi?
- Kỹ năng tôi có hiện tại đủ để ứng tuyển vị trí nào?
- Cái roadmap tôi vừa đọc — nó phù hợp với chương trình học của trường tôi không, hay chỉ
  là nội dung chung chung cho thị trường nước ngoài?

Vấn đề không phải là **lượng thông tin**, mà là **khoảng cách giữa thông tin chung và tình
huống cụ thể của từng người**.

### 1.2. Bốn biểu hiện cụ thể của vấn đề

| # | Vấn đề | Hệ quả thực tế |
| --- | --- | --- |
| **1** | **Thông tin phân mảnh** — điểm ở portal trường, deadline trong nhóm chat, lộ trình kỹ năng trong bookmark, mục tiêu trong đầu | Không ai có được một bức tranh toàn cảnh về chính mình. Sinh viên tự đánh giá tiến độ bằng cảm giác. |
| **2** | **Thông tin không kiểm chứng** — kinh nghiệm truyền miệng, roadmap ẩn danh, lời khuyên hết thời | Ra quyết định sai trên dữ liệu sai. Học một công nghệ đã lỗi thời trong 6 tháng. |
| **3** | **Mất phương hướng giữa hành trình** — biết đích ("làm backend developer") nhưng không biết bước kế tiếp | Trì hoãn, học lan man, hoặc bỏ giữa chừng vì không thấy tiến bộ. |
| **4** | **Không có phản hồi về tiến độ** — trường chỉ phản hồi bằng điểm số cuối kỳ | Phát hiện mình đi chậm khi đã quá muộn để điều chỉnh. |

### 1.3. Điều Navi **không** cố gắng giải quyết

Ranh giới rõ ràng ngay từ đầu giúp sản phẩm không bị loãng:

- Navi **không thay thế** hệ thống quản lý đào tạo của trường (LMS/portal). Navi là **lớp
  định hướng cá nhân** đặt cạnh nó.
- Navi **không dạy học**. Đã có Coursera, Udemy, YouTube làm tốt việc đó. Navi trả lời câu hỏi
  *"tôi nên học cái gì tiếp theo, và tại sao"*.
- Navi **không hứa việc làm**. Navi thu hẹp khoảng cách giữa kỹ năng hiện tại và yêu cầu thật
  của vị trí công việc, một cách minh bạch.
- Navi **không phải mạng xã hội**. Cộng đồng ở Phase 3 tồn tại để **kiểm chứng tri thức**, không
  phải để tạo tương tác.

---

## 2. Người dùng mục tiêu

### 2.1. Giai đoạn đầu (Phase 1–2): Sinh viên CNTT Việt Nam

Chọn nhóm này trước là một quyết định có chủ ý, không phải vì thuận tiện:

1. **Đây là nhóm người dùng mà team hiểu rõ nhất.** Xây sản phẩm cho vấn đề mình đang sống
   trong đó giảm rủi ro hiểu sai người dùng.
2. **Ngành CNTT có lộ trình kỹ năng rõ ràng và kiểm chứng được** — có job description thật, có
   yêu cầu kỹ thuật cụ thể. Điều này khiến giá trị cốt lõi *Trust* trở nên khả thi về mặt
   kỹ thuật ngay từ Phase 1.
3. **Nhóm này chấp nhận sản phẩm mới nhanh** và sẵn sàng cho phản hồi thẳng thắn.

### 2.2. Chân dung người dùng chính

| Nhóm | Đặc điểm | Nhu cầu chính từ Navi |
| --- | --- | --- |
| **Sinh viên năm 1–2** | Chưa rõ định hướng, dễ ngợp, chưa biết mình cần gì | *"Bắt đầu từ đâu?"* — cấu trúc, lộ trình, tránh học lan man |
| **Sinh viên năm 3–4** | Đã có định hướng, lo về năng lực thật và thời gian còn lại | *"Tôi còn thiếu gì?"* — đo khoảng cách kỹ năng, chuẩn bị đi làm |
| **Sinh viên chuyển hướng** | Bỏ hướng cũ, bắt đầu lại giữa hành trình | *"Làm lại thế nào cho hiệu quả?"* — lộ trình tính đến những gì đã có |

### 2.3. Mở rộng về sau

- **Phase 3:** sinh viên đã tốt nghiệp / đi làm — tham gia như người kiểm chứng tri thức và mentor.
- **Phase 4:** sinh viên các ngành khác ngoài CNTT; mô hình kỹ năng được thiết kế **domain-agnostic**
  ngay từ đầu để mở rộng này không cần viết lại (xem [architecture.md](architecture.md)).

---

## 3. Tầm nhìn dài hạn

### 3.1. Câu tuyên bố

> **Mọi sinh viên đều có thể nhìn rõ tiến độ của chính mình, và biết bước tiếp theo của mình
> mà không phải đoán.**

### 3.2. Bốn nấc phát triển của tầm nhìn

```
Nấc 1 — TRACKER          Navi biết mình đang ở đâu
        │                Dữ liệu học tập được tập hợp, sạch và trung thực.
        ▼
Nấc 2 — NAVIGATOR        Navi biết mình nên đi đâu tiếp
        │                Gợi ý cá nhân hóa, có giải thích, dựa trên dữ liệu thật.
        ▼
Nấc 3 — COMPANION        Navi đi cùng và học cùng mình
        │                Tri thức được kiểm chứng bởi người đã đi trước.
        ▼
Nấc 4 — LAUNCHPAD        Navi đưa mình sang bước tiếp theo của cuộc đời
                         Từ đại học sang công việc đầu tiên, có bằng chứng năng lực.
```

Mỗi nấc là điều kiện của nấc sau. **Không thể có gợi ý đáng tin (nấc 2) trên dữ liệu bẩn
(nấc 1).** Vì lý do đó, Phase 1 tập trung toàn bộ vào chất lượng và tính đầy đủ của dữ liệu,
không phải vào số lượng tính năng.

### 3.3. Điều gì phải đúng để tầm nhìn này thành hiện thực

Ghi lại các giả định để sau này kiểm chứng, thay vì mặc định là đúng:

| Giả định | Cách kiểm chứng |
| --- | --- |
| Sinh viên chịu bỏ công nhập dữ liệu học tập của mình | Đo tỉ lệ hoàn thành onboarding và retention tuần 2 ở Phase 1 |
| Một gợi ý có giải thích được tin hơn một gợi ý không giải thích | So sánh tỉ lệ chấp nhận gợi ý có/không phần "vì sao" ở Phase 2 |
| Người đã đi trước sẵn sàng đóng góp và kiểm chứng tri thức miễn phí | Thử nghiệm nhỏ với một cộng đồng có thật trước khi xây Phase 3 |

*[Inference]* Ba giả định trên dựa trên suy luận về hành vi người dùng, chưa có dữ liệu thực
nghiệm từ Navi. Chúng cần được kiểm chứng, không nên coi là sự thật.

---

## 4. Giá trị Navi mang lại

### 4.1. Cho sinh viên

| Giá trị | Trước Navi | Với Navi |
| --- | --- | --- |
| **Sự rõ ràng** | "Chắc mình đang ổn" | Thấy được: đã hoàn thành bao nhiêu, còn thiếu gì, còn bao nhiêu thời gian |
| **Tiết kiệm thời gian** | Một buổi tối tìm kiếm cho một câu hỏi | Câu trả lời trong vài giây, dựa trên dữ liệu của chính mình |
| **Ra quyết định có cơ sở** | Chọn môn/kỹ năng theo tin truyền miệng | Chọn dựa trên dữ liệu có nguồn, có trạng thái kiểm chứng |
| **Động lực bền** | Chỉ có phản hồi vào cuối kỳ | Thấy tiến bộ nhỏ mỗi tuần |
| **Bằng chứng năng lực** | CV kể chuyện | Hồ sơ kỹ năng có dấu vết hình thành thật |

### 4.2. Cách bốn Core Values được ràng buộc vào kỹ thuật

Giá trị chỉ có ý nghĩa khi nó tạo ra ràng buộc. Đây là ràng buộc tương ứng:

| Core Value | Ràng buộc kỹ thuật cụ thể |
| --- | --- |
| **Trust — Chữ Tín** | Mọi entity tri thức (curriculum, skill, career) **bắt buộc** có `source` và `verification_status ∈ {verified, community, unverified}`. Không có giá trị mặc định "verified". Khi Navi không biết, API trả về trạng thái *unknown* thay vì đoán. |
| **Speed — Tốc độ** | Chỉ số theo dõi là *time-to-answer* của người dùng, không chỉ p95 latency. Ngân sách hiệu năng được ghi trong [architecture.md](architecture.md). |
| **Innovation** | Công nghệ mới (AI, gợi ý) chỉ được thêm khi nó thay đổi được hành vi người dùng, và luôn ở dạng **giải thích được** — không có hộp đen. |
| **Practicality** | Mỗi tính năng phải viết được câu: *"Tính năng này giúp sinh viên tiến bộ thật bằng cách ___"*. Không viết được thì không làm. |

### 4.3. Định nghĩa thành công

Navi thành công **không** khi có nhiều người dùng, mà khi:

> Một sinh viên dùng Navi ba học kỳ liên tục có thể chỉ ra được một quyết định cụ thể mà họ
> đã ra **tốt hơn** nhờ Navi — và giải thích được vì sao.

---

## 5. Tài liệu liên quan

- [product-requirements.md](product-requirements.md) — vấn đề này được chuyển thành yêu cầu cụ thể như thế nào
- [roadmap.md](roadmap.md) — tầm nhìn được chia thành bốn phase
- [architecture.md](architecture.md) — kiến trúc hỗ trợ tầm nhìn này ra sao
- [glossary.md](glossary.md) — từ vựng chung của domain
