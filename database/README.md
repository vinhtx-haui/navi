# Navi Database

PostgreSQL 16 là **source of truth duy nhất** của Navi. Quyết định và các đánh đổi:
[ADR-0003](../docs/adr/0003-postgresql-as-primary-datastore.md).

**Trạng thái:** chưa có migration nào.

---

## Cấu trúc

```
database/
├── seeds/            Dữ liệu mẫu cho phát triển (CHỈ dữ liệu giả)
├── erd/              Sơ đồ quan hệ thực thể
└── README.md
```

> **Migration không nằm ở đây.** Flyway migration sống trong
> **`backend/src/main/resources/db/migration/`**. Flyway chạy lúc backend khởi động và đọc từ
> classpath, nên đặt trong resources làm jar tự chứa được toàn bộ schema; đặt ngoài `backend/` sẽ
> buộc bản deploy mang thêm file rời — một cách để môi trường lệch nhau. Quy ước bên dưới áp dụng
> cho các file ở đó.

## Quy ước migration

| Quy ước | Chi tiết |
| --- | --- |
| Đặt tên | `V<số>__<mô_tả_ngắn>.sql` — ví dụ `V1__baseline.sql`, `V2__add_skill_module.sql` |
| Số version | Tăng dần, **không dùng lại số cũ** |
| Đã chạy production | **Không bao giờ sửa.** Sai thì viết migration mới để sửa |
| Nội dung | SQL thuần. Không phụ thuộc ORM |
| Rollback | Viết migration bù (forward-fix), không dựa vào undo |
| Test | Mọi migration phải chạy được trên database rỗng **và** trên bản sao dữ liệu hiện có |

## Quy ước schema

Các quy ước dưới đây áp dụng cho **mọi** bảng:

| Hạng mục | Quy ước | Lý do |
| --- | --- | --- |
| Tên bảng | `snake_case`, số nhiều | Nhất quán với [glossary.md](../docs/glossary.md) |
| Tên cột | `snake_case`, số ít | |
| Khóa chính | `id UUID` (UUID v7) | Không lộ thông tin qua id tuần tự; sắp xếp được theo thời gian |
| Audit | `created_at`, `updated_at` — `TIMESTAMPTZ NOT NULL` | Điều tra sự cố |
| Xóa | `deleted_at TIMESTAMPTZ NULL` (xóa mềm) cho dữ liệu người dùng | Sinh viên xóa nhầm cả một học kỳ là mất mát thật |
| Schema | Tách theo module: `identity.`, `academic.`, `progress.`, `goal.`, `skill.`, `knowledge.` | Biên giới module hiện diện ở cả tầng database |
| Ràng buộc | Dùng `FOREIGN KEY`, `NOT NULL`, `CHECK` ở tầng database | Toàn vẹn do database bảo đảm, không phụ thuộc việc app có nhớ kiểm tra |

### Quy ước riêng cho dữ liệu tri thức

Mọi bảng chứa dữ liệu tri thức (`curricula`, `courses`, `skills`, `roadmaps`, và sau này
`role_requirements`) **bắt buộc**:

```sql
source_id           UUID        NOT NULL REFERENCES knowledge.sources(id),
verification_status VARCHAR(20) NOT NULL
                    CHECK (verification_status IN ('VERIFIED','COMMUNITY','UNVERIFIED')),
verified_at         TIMESTAMPTZ NULL,
verified_by         VARCHAR(255) NULL
```

> **Không đặt `DEFAULT 'VERIFIED'`** cho `verification_status`, và không đặt default nào khác.
> Đây là cách giá trị *Trust* được ràng buộc ở tầng thấp nhất: quên ghi nguồn sẽ thành lỗi ngay
> tại lúc ghi, thay vì âm thầm tạo ra một bản ghi trông như đã kiểm chứng.

## Dữ liệu thật của người dùng — quy tắc tuyệt đối

Dữ liệu học tập là **dữ liệu cá nhân**.

- `seeds/` **chỉ** chứa dữ liệu giả (synthetic). Không dùng dữ liệu của người thật, kể cả của
  chính mình, kể cả đã "làm mờ".
- Dump database, export CSV từ production **không bao giờ** được commit. `.gitignore` đã loại trừ
  `database/seeds/real/`, `*.dump`, `*.sql.gz`.
- Trước mỗi commit: kiểm tra không có file dữ liệu nào lọt vào staging area.

## Chạy database ở local

Dùng Docker Compose trong [infra/](../infra/):

```bash
docker compose -f infra/docker/docker-compose.dev.yml up -d
```

```bash
docker compose -f infra/docker/docker-compose.dev.yml down
```

Thông tin kết nối mặc định cho **môi trường dev local**: xem file compose. Các giá trị đó chỉ
dành cho local; production dùng secret riêng, không nằm trong repo.

## ERD

`erd/` sẽ chứa sơ đồ quan hệ thực thể. Sơ đồ lõi Phase 1 (dạng text) hiện nằm trong
[docs/architecture.md](../docs/architecture.md) §5.2.
