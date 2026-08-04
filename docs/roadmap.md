# Roadmap

Roadmap này mô tả **mục tiêu** của từng phase, không mô tả chi tiết implementation. Mỗi phase
là một nấc của tầm nhìn trong [vision.md](vision.md), và **chỉ bắt đầu khi phase trước đã đạt
tiêu chí hoàn thành** — vì mỗi phase phụ thuộc vào chất lượng dữ liệu của phase trước.

- **Trạng thái hiện tại:** Phase 0 — Project initialisation (tài liệu nền tảng)
- **Cập nhật:** 2026-08-04

> **Về thời lượng:** roadmap này không ghi deadline theo ngày. Đây là dự án dài hạn được phát
> triển song song với việc học; đặt deadline cứng lúc này sẽ tạo cam kết giả. Mỗi phase kết
> thúc bằng **tiêu chí đo được**, không bằng ngày trên lịch.

---

## Tổng quan

| Phase | Tên | Nấc tầm nhìn | Câu hỏi phase này trả lời |
| --- | --- | --- | --- |
| **1** | Foundation | Tracker | *Tôi đang ở đâu?* |
| **2** | Intelligence | Navigator | *Tôi nên đi đâu tiếp, và vì sao?* |
| **3** | Community | Companion | *Ai đã đi con đường này, và họ học được gì?* |
| **4** | Career Growth | Launchpad | *Tôi đã sẵn sàng cho công việc đầu tiên chưa?* |

```
Phase 1              Phase 2              Phase 3              Phase 4
FOUNDATION    ──▶    INTELLIGENCE   ──▶   COMMUNITY      ──▶   CAREER GROWTH
dữ liệu thật         gợi ý có giải thích  tri thức kiểm chứng  cầu nối sang nghề
```

---

## Phase 1 — Foundation

> **Mục tiêu:** Cho sinh viên một bức tranh **trung thực và đầy đủ** về hiện tại học tập của
> mình — và đặt nền dữ liệu để mọi phase sau có thể tin cậy được.

### Vì sao phase này quan trọng nhất

Đây là phase dễ bị coi nhẹ nhất và cũng là phase quyết định nhất. Mọi gợi ý thông minh ở
Phase 2, mọi so sánh cộng đồng ở Phase 3, mọi đánh giá năng lực ở Phase 4 đều đọc từ dữ liệu
sinh ra ở đây. **Dữ liệu sai ở Phase 1 sẽ được khuếch đại thành lời khuyên sai ở Phase 2.**

### Phạm vi

- **Tài khoản & hồ sơ học tập** — đăng ký, đăng nhập, thông tin ngành/khóa/trường.
- **Quản lý môn học** — môn đã học, đang học, dự kiến học; tín chỉ, điểm, học kỳ, môn tiên quyết.
- **Theo dõi tiến độ** — tiến độ tín chỉ theo chương trình đào tạo, GPA theo thời gian, vị trí
  hiện tại trong hành trình.
- **Quản lý mục tiêu** — mục tiêu dài hạn chia thành mục tiêu nhỏ có thể hoàn thành; theo dõi
  trạng thái.
- **Roadmap kỹ năng (thủ công)** — lộ trình kỹ năng do người dùng tự tạo hoặc chọn từ template
  đã được kiểm chứng; đánh dấu mức độ thành thạo.
- **Dashboard** — một màn hình trả lời được câu hỏi *"tôi đang ở đâu"*.

### Nằm ngoài phạm vi Phase 1

Ghi rõ để chống phình phạm vi: chưa có AI, chưa có gợi ý tự động, chưa có tính năng xã hội,
chưa có mobile app, chưa tích hợp trực tiếp với portal của trường.

### Tiêu chí hoàn thành

- [ ] Một sinh viên nhập được toàn bộ dữ liệu học tập của mình và onboarding dưới 10 phút.
- [ ] Dashboard trả lời đúng ba câu: đã hoàn thành bao nhiêu tín chỉ, còn thiếu gì, GPA đang đi theo hướng nào.
- [ ] Mọi dữ liệu curriculum/skill trong hệ thống có `source` và `verification_status` — không có bản ghi nào thiếu.
- [ ] Ít nhất **5 người dùng thật** (không phải team) dùng liên tục 2 tuần và cho phản hồi.
- [ ] Test coverage của domain logic đạt mức đã thống nhất; integration test chạy trên PostgreSQL thật.

---

## Phase 2 — Intelligence

> **Mục tiêu:** Navi chuyển từ *ghi nhận* sang *cố vấn* — đưa ra gợi ý cá nhân hóa, và **luôn
> giải thích được vì sao**.

### Phạm vi

- **Gợi ý bước tiếp theo** — dựa trên dữ liệu học tập, mục tiêu và tiến độ thật của người dùng.
- **Phát hiện khoảng cách kỹ năng** — so sánh kỹ năng hiện có với kỹ năng mục tiêu yêu cầu.
- **Insight về tiến độ** — phát hiện dấu hiệu chậm tiến độ **trước khi** quá muộn để điều chỉnh.
- **Sinh roadmap tự động** — từ mục tiêu, sinh lộ trình có thứ tự, tính đến những gì đã hoàn thành.
- **AI Assistant** — trả lời câu hỏi của sinh viên, **được neo vào dữ liệu thật của họ và dữ liệu
  đã kiểm chứng của hệ thống** (retrieval-based), không phải trả lời chung chung.

### Nguyên tắc bất di bất dịch của phase này

1. **Mọi gợi ý phải kèm lý do.** Không có gợi ý "hộp đen". Nếu không giải thích được, không hiện ra.
2. **AI không được bịa.** Khi không có dữ liệu, assistant trả lời *"tôi không có thông tin này"*.
3. **Người dùng luôn có thể bác bỏ.** Gợi ý là gợi ý, không phải mệnh lệnh; hệ thống học từ việc bị từ chối.

### Tiêu chí hoàn thành

- [ ] 100% gợi ý hiển thị được nguồn dữ liệu và lý do sinh ra nó.
- [ ] Tỉ lệ người dùng chấp nhận gợi ý được đo và vượt ngưỡng cơ sở đã đặt ở đầu phase.
- [ ] AI assistant có cơ chế phát hiện "ngoài phạm vi hiểu biết" và từ chối trả lời thay vì đoán.
- [ ] Không có sự cố nào mà hệ thống đưa thông tin sai gây quyết định sai (theo dõi qua báo lỗi người dùng).

---

## Phase 3 — Community

> **Mục tiêu:** Biến kinh nghiệm của người đã đi trước thành **tri thức có thể kiểm chứng và
> tái sử dụng** — thay vì kinh nghiệm truyền miệng mất đi sau mỗi khóa.

### Phạm vi

- **Roadmap chia sẻ** — người dùng công khai lộ trình của mình; người khác fork và điều chỉnh.
- **Tri thức được kiểm chứng bởi cộng đồng** — nội dung do người dùng đóng góp đi qua quy trình
  review trước khi đổi trạng thái từ `community` sang `verified`.
- **So sánh ẩn danh** — người dùng tự đặt mình trong bối cảnh chung (cùng ngành, cùng khóa) mà
  không lộ danh tính người khác.
- **Hỏi đáp theo ngữ cảnh** — câu hỏi gắn với môn học/kỹ năng cụ thể, không phải diễn đàn chung.

### Ràng buộc thiết kế

- **Chống nhiễu là yêu cầu bậc nhất, không phải tính năng phụ.** Nội dung không kiểm chứng
  **không được** hiển thị ngang hàng nội dung đã kiểm chứng.
- **Quyền riêng tư mặc định là kín.** Không có dữ liệu nào bị chia sẻ khi người dùng chưa chủ động bật.
- Cộng đồng phục vụ mục tiêu *Trust*, không phục vụ chỉ số tương tác. Không có tính năng nào
  được thiết kế để tăng thời gian ở lại app.

### Tiêu chí hoàn thành

- [ ] Có quy trình kiểm chứng nội dung hoạt động thật, với người kiểm chứng thật.
- [ ] Tỉ lệ nội dung `verified` trên tổng nội dung cộng đồng đạt ngưỡng đã đặt.
- [ ] Không có rò rỉ dữ liệu cá nhân trong các tính năng so sánh (kiểm chứng bằng review bảo mật).

---

## Phase 4 — Career Growth

> **Mục tiêu:** Thu hẹp khoảng cách giữa **"tốt nghiệp"** và **"được nhận việc"** một cách
> minh bạch và có bằng chứng.

### Phạm vi

- **Career roadmap** — bản đồ từ kỹ năng hiện có đến các vai trò nghề cụ thể (backend, data, mobile…).
- **Skill-to-role mapping** — đối chiếu kỹ năng của người dùng với yêu cầu **thật** của vị trí,
  lấy từ nguồn kiểm chứng được, có ghi thời điểm cập nhật.
- **Portfolio tự động** — hồ sơ năng lực sinh ra từ dấu vết tiến bộ thật trong Navi, không phải
  từ khai báo.
- **Mentor network** — kết nối sinh viên với người đi trước trong ngành, có cấu trúc và mục tiêu rõ.

### Ràng buộc quan trọng

Dữ liệu nghề nghiệp **hết hạn nhanh**. Yêu cầu công việc năm nay khác năm sau. Vì vậy mọi dữ
liệu career **bắt buộc** có timestamp và nguồn; dữ liệu quá cũ phải bị đánh dấu là cũ, không
được hiển thị như dữ liệu hiện hành. Navi **không hứa việc làm** — Navi chỉ làm rõ khoảng cách.

### Tiêu chí hoàn thành

- [ ] Người dùng nhìn được khoảng cách cụ thể giữa mình và một vai trò cụ thể, kèm nguồn dữ liệu.
- [ ] Mọi dữ liệu yêu cầu công việc có nguồn và ngày cập nhật; có cơ chế phát hiện dữ liệu cũ.
- [ ] Có ít nhất một trường hợp người dùng dùng portfolio từ Navi trong quá trình ứng tuyển thật.

---

## Nguyên tắc vận hành roadmap

1. **Không nhảy phase.** Xây tính năng Phase 2 khi Phase 1 chưa có dữ liệu sạch sẽ tạo ra lời
   khuyên không đáng tin — vi phạm giá trị *Trust*.
2. **Mỗi phase phải có người dùng thật trước khi chuyển phase.** Không có người dùng thật thì
   không có phản hồi thật, và phase sau sẽ xây trên phỏng đoán.
3. **Roadmap là tài liệu sống.** Khi thay đổi hướng, sửa file này và ghi lý do — không xóa
   dấu vết quyết định cũ.
4. **Mỗi thay đổi kiến trúc lớn phải có ADR.** Xem [adr/](adr/).
