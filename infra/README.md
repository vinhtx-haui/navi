# Navi Infrastructure

Hạ tầng cho phát triển và triển khai.

**Nguyên tắc chi phối:** *chọn độ phức tạp muộn nhất có thể*. Mỗi thành phần hạ tầng phải trả lời
được câu hỏi *"vấn đề thật nào đang xảy ra mà thành phần này giải quyết?"*. Không trả lời được thì
không thêm.

---

## Cấu trúc

```
infra/
├── docker/
│   └── docker-compose.dev.yml    PostgreSQL + Redis cho môi trường dev local
├── ci/                            GitHub Actions workflow (sẽ thêm)
└── README.md
```

## Môi trường dev local

```bash
docker compose -f infra/docker/docker-compose.dev.yml up -d
```

Dựng lên:

| Service | Port | Vai trò |
| --- | --- | --- |
| PostgreSQL 16 | 5432 | Database chính |
| Redis 7 | 6379 | Cache / rate limit — **chỉ bật khi đã có nhu cầu thật** |

Kiểm tra trạng thái:

```bash
docker compose -f infra/docker/docker-compose.dev.yml ps
```

Xóa toàn bộ dữ liệu local và dựng lại từ đầu:

```bash
docker compose -f infra/docker/docker-compose.dev.yml down -v
```

> Thông tin đăng nhập trong file compose **chỉ dùng cho local**. Chúng nằm trong repo có chủ ý,
> vì chúng không bảo vệ gì cả — database chỉ lắng nghe trên máy dev. Production dùng secret riêng,
> **không** nằm trong repo.

## Triển khai — Phase 1

| Hạng mục | Kế hoạch |
| --- | --- |
| Hình thức | Container trên **một VPS** |
| Reverse proxy | Caddy hoặc Nginx, TLS tự động |
| Database | PostgreSQL trên cùng host, hoặc managed PostgreSQL |
| Backup | Backup định kỳ + **kiểm tra khôi phục thật** (backup chưa khôi phục thử thì chưa phải backup) |
| Secret | Biến môi trường trên host, không trong repo |
| CI | GitHub Actions — build + test trên mỗi PR |
| CD | Thủ công ở Phase 1; tự động khi quy trình đã ổn định |

**Kubernetes nằm ngoài phạm vi.** Ở quy mô Phase 1, chi phí vận hành của nó lớn hơn mọi lợi ích
mà người dùng cảm nhận được. Điều kiện xem xét lại: cần nhiều node, nhiều môi trường, hoặc
autoscaling thật — xem [ADR-0001](../docs/adr/0001-modular-monolith.md).

## Checklist trước khi lên production lần đầu

- [ ] Không có secret nào trong repo (kiểm bằng công cụ quét secret, không chỉ bằng mắt)
- [ ] Database có backup tự động, **và đã thử khôi phục thành công một lần**
- [ ] HTTPS bật, HTTP redirect sang HTTPS
- [ ] Rate limit trên endpoint auth
- [ ] Health/readiness endpoint hoạt động
- [ ] Structured logging có correlation id
- [ ] `spring.jpa.hibernate.ddl-auto` **không** phải `update` hay `create` ở bất kỳ profile nào
- [ ] Không có dữ liệu thật của người dùng trong seed hay test fixture
