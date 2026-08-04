# Glossary — Từ vựng domain

Tài liệu này định nghĩa từ vựng chung của Navi. Mục đích: **một khái niệm — một tên gọi**, dùng
nhất quán trong tài liệu, tên bảng, tên class và tên endpoint.

Lý do cần từ vựng chung ngay từ đầu: khi cùng một thứ có ba tên (`subject`, `course`, `module`),
codebase sẽ tích lũy ba mô hình dữ liệu hơi khác nhau, và bug xuất hiện ở chỗ chúng gặp nhau.

---

## Academic — Học tập

| Thuật ngữ (EN) | Tiếng Việt | Định nghĩa trong Navi |
| --- | --- | --- |
| **Curriculum** | Chương trình đào tạo | Tập hợp yêu cầu tốt nghiệp của một ngành/khóa cụ thể. Có version và `provenance`. |
| **Course** | Môn học | Một môn trong curriculum. Có mã, tên, số tín chỉ, môn tiên quyết. Là **định nghĩa**, không phải việc học của một người. |
| **Enrollment** | Việc học một môn | Quan hệ giữa **một người dùng** và **một môn**: trạng thái, học kỳ, điểm. Phân biệt rõ với `Course`. |
| **Credit** | Tín chỉ | Đơn vị khối lượng học tập. Value object, không phải số nguyên thuần. |
| **Semester** | Học kỳ | Đơn vị thời gian học tập. Có thứ tự trong hành trình của người dùng. |
| **Prerequisite** | Môn tiên quyết | Quan hệ: môn A phải hoàn thành trước môn B. |
| **Grade** | Điểm | Kết quả của một enrollment. Lưu kèm thang điểm gốc của trường. |
| **GPA Scale** | Thang điểm | Thang quy đổi (4.0, 10.0…). Lưu tường minh để không quy đổi sai. |

> **Quy ước quan trọng:** `Course` là định nghĩa môn học; `Enrollment` là việc một người học môn
> đó. Gộp hai khái niệm này là lỗi thiết kế thường gặp và tốn kém để sửa về sau.

## Progress — Tiến độ

| Thuật ngữ (EN) | Tiếng Việt | Định nghĩa trong Navi |
| --- | --- | --- |
| **Progress Snapshot** | Ảnh chụp tiến độ | Trạng thái tiến độ tại một thời điểm. Cho phép nhìn tiến bộ theo thời gian. |
| **Credit Progress** | Tiến độ tín chỉ | Tín chỉ đã hoàn thành / tín chỉ yêu cầu. **Chỉ tính được khi có curriculum.** |
| **Insight** | Nhận định | Một quan sát Navi đưa ra từ dữ liệu (ví dụ: xu hướng GPA giảm). Bắt buộc kèm dữ liệu cơ sở. |

## Goal — Mục tiêu

| Thuật ngữ (EN) | Tiếng Việt | Định nghĩa trong Navi |
| --- | --- | --- |
| **Goal** | Mục tiêu | Điều người dùng muốn đạt được, có mốc thời gian. |
| **Subgoal** | Mục tiêu con | Bước cụ thể thuộc một Goal. Đơn vị hoàn thành thực tế. |
| **Goal Progress** | Tiến độ mục tiêu | **Được tính** từ subgoal đã hoàn thành — người dùng **không** tự khai phần trăm. |

## Skill — Kỹ năng

| Thuật ngữ (EN) | Tiếng Việt | Định nghĩa trong Navi |
| --- | --- | --- |
| **Skill** | Kỹ năng | Một năng lực cụ thể, học được và đánh giá được. Định nghĩa **không** gắn cứng vào ngành CNTT. |
| **Roadmap** | Lộ trình | Chuỗi bước có thứ tự dẫn tới một mục tiêu kỹ năng. Có `provenance`. |
| **Roadmap Step** | Bước lộ trình | Một bước trong roadmap, gắn với một skill. |
| **Proficiency** | Mức thành thạo | Mức độ nắm một skill, theo thang bên dưới. |
| **Skill Gap** | Khoảng cách kỹ năng | Chênh lệch giữa proficiency hiện tại và proficiency mục tiêu yêu cầu. |

### Thang Proficiency

Thang cố định, dùng thống nhất toàn hệ thống. Định nghĩa dựa trên **việc làm được**, không dựa
trên cảm nhận:

| Mức | Tên | Định nghĩa hành vi |
| --- | --- | --- |
| 0 | **Unknown** | Chưa tiếp xúc |
| 1 | **Aware** | Biết khái niệm, chưa dùng |
| 2 | **Learning** | Đang học, làm được khi có hướng dẫn |
| 3 | **Applied** | Tự làm được trong bài tập / project cá nhân |
| 4 | **Proficient** | Làm được trong bối cảnh thật, xử lý được trường hợp ngoại lệ |
| 5 | **Teaching** | Giải thích và hướng dẫn được người khác |

## Trust & Knowledge — Tri thức và kiểm chứng

| Thuật ngữ (EN) | Tiếng Việt | Định nghĩa trong Navi |
| --- | --- | --- |
| **Source** | Nguồn | Xuất xứ của một mẩu tri thức (trường đại học, tin tuyển dụng, đóng góp cộng đồng…). |
| **Provenance** | Xuất xứ | Bộ thông tin đầy đủ: nguồn + trạng thái kiểm chứng + thời điểm + người kiểm chứng. |
| **Verification Status** | Trạng thái kiểm chứng | `VERIFIED` · `COMMUNITY` · `UNVERIFIED`. Xem bên dưới. |
| **Unknown state** | Trạng thái không biết | Khi Navi thiếu dữ liệu để trả lời. Được trả về **tường minh**, không thay bằng 0 hay giá trị suy đoán. |

### Verification Status

| Trạng thái | Ý nghĩa | Cách hiển thị |
| --- | --- | --- |
| `VERIFIED` | Đã đối chiếu với nguồn chính thức, có ghi thời điểm và người/quy trình kiểm chứng | Hiển thị bình thường, có nhãn nguồn |
| `COMMUNITY` | Do cộng đồng đóng góp và đã qua review, nhưng không phải nguồn chính thức | Hiển thị kèm nhãn phân biệt rõ |
| `UNVERIFIED` | Chưa kiểm chứng | **Không** hiển thị ngang hàng với dữ liệu đã kiểm chứng; phải có cảnh báo |

**Không có giá trị mặc định.** Mọi bản ghi tri thức phải nêu rõ trạng thái của nó tại thời điểm ghi.

## Recommendation — Gợi ý (Phase 2)

| Thuật ngữ (EN) | Tiếng Việt | Định nghĩa trong Navi |
| --- | --- | --- |
| **Recommendation** | Gợi ý | Hành động Navi đề xuất. **Bắt buộc** có `reasoning` không rỗng. |
| **Reasoning** | Lý do | Danh sách căn cứ dẫn tới gợi ý, viết cho người dùng đọc được. |
| **Time-to-answer** | Thời gian tới câu trả lời | Chỉ số của giá trị *Speed*: từ lúc người dùng có câu hỏi đến lúc có câu trả lời dùng được. Khác với latency của API. |

## Career — Nghề nghiệp (Phase 4)

| Thuật ngữ (EN) | Tiếng Việt | Định nghĩa trong Navi |
| --- | --- | --- |
| **Role** | Vai trò nghề | Một vị trí công việc cụ thể (ví dụ: Backend Developer — Fresher). |
| **Role Requirement** | Yêu cầu vai trò | Skill + proficiency tối thiểu cho một role. **Bắt buộc** có nguồn và ngày cập nhật. |
| **Portfolio** | Hồ sơ năng lực | Bằng chứng năng lực sinh ra từ dấu vết tiến bộ thật trong Navi, không từ khai báo. |
| **Mentor** | Người hướng dẫn | Người đi trước trong ngành, tham gia hỗ trợ có cấu trúc. |

---

## Quy ước đặt tên trong code

| Ngữ cảnh | Quy ước | Ví dụ |
| --- | --- | --- |
| Java class | PascalCase, danh từ đơn | `Course`, `Enrollment`, `ProgressSnapshot` |
| Java package | lowercase, tên module | `com.navi.academic.domain` |
| Bảng database | snake_case, số nhiều | `courses`, `progress_snapshots` |
| Cột database | snake_case, số ít | `credit_count`, `verification_status` |
| REST endpoint | kebab-case, số nhiều | `/api/v1/courses`, `/api/v1/progress-snapshots` |
| JSON field | camelCase | `creditCount`, `verificationStatus` |
| Enum | SCREAMING_SNAKE_CASE | `VERIFIED`, `IN_PROGRESS` |

**Nguyên tắc:** tên trong code phải trùng với tên trong glossary này. Nếu cần một tên khác, sửa
glossary trước, rồi sửa code — không để hai từ vựng song song tồn tại.
