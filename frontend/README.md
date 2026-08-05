# Navi Frontend

Next.js web client. **Deliberately thin** — the centre of gravity of this project is the backend.

**Status:** shell runs and talks to the API. No product feature is implemented yet.

---

## Vai trò của frontend trong dự án này

Frontend tồn tại để **chứng minh sản phẩm chạy thật** và để người dùng thật dùng được — không phải
để trình diễn kỹ thuật frontend. Bốn nguyên tắc:

- **Không có business logic ở frontend.** Tính GPA, tính tiến độ, kiểm tra tiên quyết đều ở backend.
  Frontend hiển thị kết quả. Logic tồn tại ở hai nơi là logic sẽ tự mâu thuẫn với chính nó.
- **Trạng thái kiểm chứng phải hiển thị.** Đây là yêu cầu sản phẩm, không phải chi tiết UI: dữ liệu
  `COMMUNITY` hoặc `UNVERIFIED` không được trông giống `VERIFIED`. Xem
  [`VerificationBadge`](src/components/VerificationBadge.tsx).
- **Trạng thái "không biết" phải hiển thị được, kèm lý do.** Khi backend không có dữ liệu, UI nói rõ
  thiếu gì — không hiện `0`, không hiện ô trống, không bịa giá trị mặc định. Xem `Answer<T>` trong
  [`types.ts`](src/lib/api/types.ts) và cách `page.tsx` xử lý cả hai nhánh.
- **Type sinh từ OpenAPI spec của backend** khi spec có (Phase 1). Hiện tại type viết tay trong
  `src/lib/api/types.ts` và sẽ được thay thế — hợp đồng API chỉ nên có một nguồn.

## Stack

Kiểm tra ngày 2026-08-05:

| Thành phần | Phiên bản |
| --- | --- |
| Next.js | 16.3.0 (App Router) |
| React | 19.2.8 |
| TypeScript | 5.x (strict) |
| Tailwind CSS | 4.x |
| Node | 22.21.1 |

> Next 16 có breaking change so với các bản trước. Tài liệu bản đúng nằm ngay trong
> `node_modules/next/dist/docs/` — đọc ở đó thay vì dựa vào ký ức về Next 13/14/15.
> `AGENTS.md` / `CLAUDE.md` trong thư mục này do `next dev` tự sinh và tự thêm lại.

## Chạy

```bash
npm run dev
```

Mở `http://localhost:3000`. Trang chủ gọi `GET /api/v1/meta` của backend; nếu backend chưa chạy,
trang vẫn trả 200 và **nói rõ là chưa kết nối được** thay vì hiện dữ liệu giả.

Backend cần chạy trước — xem [backend/README.md](../backend/README.md).

```bash
npm run build && npx next start
```

```bash
npx tsc --noEmit && npm run lint
```

## Cấu hình

```bash
cp .env.example .env.local
```

| Biến | Mặc định | Ghi chú |
| --- | --- | --- |
| `NEXT_PUBLIC_API_BASE_URL` | `http://localhost:8080` | `NEXT_PUBLIC_*` được nhúng vào bundle trình duyệt → **public**. Không bao giờ đặt secret sau tiền tố này. |

## Cấu trúc

```
frontend/
├── src/
│   ├── app/
│   │   ├── layout.tsx          # metadata, font, lang="vi"
│   │   ├── page.tsx            # shell: trạng thái backend + quy ước hiển thị
│   │   └── globals.css
│   ├── components/
│   │   └── VerificationBadge.tsx
│   └── lib/api/
│       ├── client.ts           # apiFetch, ApiError, ApiUnreachableError
│       └── types.ts            # ApiMeta, ProblemDetail, Provenance, Answer<T>
├── .env.example
└── README.md
```

`ApiError` (backend trả lỗi) và `ApiUnreachableError` (không gọi được backend) là hai lớp riêng có
chủ ý: với người dùng đó là hai vấn đề khác nhau và cần hai thông báo khác nhau.

## Ưu tiên màn hình (Phase 1)

Theo use case trong [product-requirements.md](../docs/product-requirements.md):

1. **Auth** — đăng ký, đăng nhập
2. **Onboarding** — nhập hồ sơ học tập ban đầu (mục tiêu: dưới 10 phút)
3. **Dashboard** — trả lời *"tôi đang ở đâu"* trong một màn hình
4. **Course management** — danh sách và nhập môn học theo học kỳ
5. **Goals** — mục tiêu và mục tiêu con
6. **Skill roadmap** — lộ trình kỹ năng

## Lưu ý về hiệu năng

Chỉ số của giá trị *Speed* là **time-to-answer** của người dùng, không phải latency của API. Ở
frontend cụ thể là: số lần click để tới thông tin quan trọng nhất phải nhỏ. Một dashboard cần 5 lần
click để xem tiến độ là sản phẩm chậm, dù API trả về trong 50 ms.

Mục tiêu Phase 1: nội dung chính của dashboard hiển thị dưới 1.5 s trên mạng 4G.
*[Unverified]* Đây là mục tiêu thiết kế, chưa được đo trên hệ thống thật.
