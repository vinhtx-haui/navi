# Navi Frontend

Next.js web client. **Được giữ mỏng có chủ ý** — trung tâm của dự án là backend.

**Trạng thái:** chưa khởi tạo.

---

## Vai trò của frontend trong dự án này

Frontend tồn tại để **chứng minh sản phẩm chạy thật** và để người dùng thật dùng được — không
phải để trình diễn kỹ thuật frontend. Nguyên tắc:

- **Không có business logic ở frontend.** Tính GPA, tính tiến độ, kiểm tra tiên quyết đều nằm ở
  backend. Frontend hiển thị kết quả. Lý do: nếu logic tồn tại ở hai nơi, hai nơi sẽ lệch nhau.
- **Type sinh từ OpenAPI spec của backend**, không viết tay. Hợp đồng API là một nguồn duy nhất.
- **Trạng thái kiểm chứng phải được hiển thị.** Đây là yêu cầu sản phẩm, không phải chi tiết UI:
  dữ liệu `COMMUNITY` hoặc `UNVERIFIED` không được trông giống dữ liệu `VERIFIED`. Xem
  [docs/glossary.md](../docs/glossary.md).
- **Trạng thái `UNKNOWN` phải hiển thị được.** Khi backend trả về "không biết", UI nói "chưa có
  dữ liệu" — không hiển thị `0`, không hiển thị ô trống không giải thích.

## Stack dự kiến

| Thành phần | Lựa chọn | Ghi chú |
| --- | --- | --- |
| Framework | Next.js 15 (App Router) | SSR cho trang public, client-side cho dashboard |
| Ngôn ngữ | TypeScript (strict) | `any` là dấu hiệu cần xem lại, không phải giải pháp |
| Styling | Tailwind CSS | Đủ nhanh, không cần tự dựng design system ở giai đoạn này |
| Data fetching | TanStack Query | Cache, retry, trạng thái loading/error có cấu trúc |
| Form | React Hook Form + Zod | Validate ở client; **backend vẫn validate độc lập** |
| API client | Sinh từ OpenAPI spec | Type-safe, tự đồng bộ khi backend đổi |

Môi trường đã kiểm tra: **Node v22.21.1** ✅

## Cấu trúc dự kiến

```
frontend/
├── package.json
├── src/
│   ├── app/                    # Next.js App Router
│   │   ├── (auth)/             # login, register
│   │   └── (dashboard)/        # màn hình sau đăng nhập
│   ├── components/
│   │   ├── ui/                 # primitive dùng chung
│   │   └── domain/             # component theo domain (CourseCard, ProgressRing…)
│   ├── lib/
│   │   ├── api/                # client sinh từ OpenAPI
│   │   └── auth/               # xử lý token
│   └── types/                  # type sinh tự động — không sửa tay
└── README.md
```

## Ưu tiên màn hình (Phase 1)

Thứ tự này bám theo use case trong [product-requirements.md](../docs/product-requirements.md):

1. **Auth** — đăng ký, đăng nhập
2. **Onboarding** — nhập hồ sơ học tập ban đầu (mục tiêu: dưới 10 phút)
3. **Dashboard** — trả lời *"tôi đang ở đâu"* trong một màn hình
4. **Course management** — danh sách và nhập môn học theo học kỳ
5. **Goals** — mục tiêu và mục tiêu con
6. **Skill roadmap** — lộ trình kỹ năng

## Lưu ý về hiệu năng

Chỉ số của giá trị *Speed* là **time-to-answer** của người dùng, không phải latency của API.
Cụ thể ở frontend: số lần click để tới thông tin quan trọng nhất phải nhỏ. Một dashboard cần 5
lần click để xem tiến độ là sản phẩm chậm, dù API trả về trong 50 ms.

Mục tiêu Phase 1: nội dung chính của dashboard hiển thị dưới 1.5 s trên mạng 4G.
*[Unverified]* Đây là mục tiêu thiết kế, chưa được đo trên hệ thống thật.
