# Contributing to Navi

Hiện tại Navi được phát triển bởi một người. Tài liệu này chủ yếu là **hợp đồng với chính mình
trong tương lai** — để chất lượng không phụ thuộc vào việc hôm đó có nhớ quy tắc hay không.

---

## Nguyên tắc làm việc

1. **Tài liệu đi trước quyết định lớn.** Quyết định kiến trúc có ADR trước khi có code.
2. **Ship lát mỏng.** Mỗi thay đổi phải là một thứ chạy được và kiểm chứng được, không phải nửa
   tính năng.
3. **Domain logic có test.** Nếu logic quan trọng mà không test được, đó là dấu hiệu thiết kế cần
   sửa, không phải lý do bỏ test.
4. **Không "sẽ dọn sau".** Nợ kỹ thuật trên dự án cá nhân dài hạn ít khi được trả.

## Quy tắc kỹ thuật bắt buộc

Xem đầy đủ trong [backend/README.md](backend/README.md). Ba quy tắc quan trọng nhất:

- `domain` không import Spring, không import JPA.
- Module chỉ gọi module khác qua `XModuleApi`.
- Mọi entity tri thức có `source` và `verification_status` — không có giá trị mặc định.

## Commit convention

Dùng [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <mô tả ngắn ở thể mệnh lệnh>
```

| Type | Dùng khi |
| --- | --- |
| `feat` | Thêm tính năng |
| `fix` | Sửa lỗi |
| `docs` | Chỉ thay đổi tài liệu |
| `refactor` | Đổi cấu trúc code, không đổi hành vi |
| `test` | Thêm hoặc sửa test |
| `chore` | Build, dependency, cấu hình |
| `perf` | Cải thiện hiệu năng |

Scope là tên module: `identity`, `academic`, `progress`, `goal`, `skill`, `knowledge`, `infra`, `docs`.

Ví dụ:

```
feat(academic): add course prerequisite validation
fix(progress): correct GPA calculation for retaken courses
docs(adr): add ADR-0004 on caching strategy
```

## Checklist trước mỗi commit

- [ ] Build và test pass (`./mvnw verify`)
- [ ] Không có secret, token, mật khẩu trong diff
- [ ] Không có dữ liệu thật của người dùng trong diff
- [ ] Không có `System.out.println` / `console.log` còn sót
- [ ] Tài liệu được cập nhật nếu hành vi thay đổi
- [ ] Có ADR nếu đây là quyết định kiến trúc

## Branch

| Branch | Vai trò |
| --- | --- |
| `main` | Luôn ở trạng thái build được. Không commit trực tiếp khi đã có CI |
| `feat/<tên>` | Tính năng |
| `fix/<tên>` | Sửa lỗi |
| `docs/<tên>` | Tài liệu |

## Về nhiều tài khoản GitHub

Repo này thuộc tài khoản GitHub dành riêng cho việc học tập và phát triển Navi, **không** phải tài
khoản cá nhân. Trước commit đầu tiên và sau mỗi lần đổi máy, kiểm tra:

```bash
git config user.name && git config user.email && git remote -v
```

Nên cấu hình `user.name` / `user.email` **ở cấp repository** (không dùng `--global`) để tránh
commit bằng danh tính sai:

```bash
git config user.email "email-cua-tai-khoan-navi@example.com"
```
