import { apiFetch, API_BASE_URL, ApiError, ApiUnreachableError } from '@/lib/api/client';
import { VerificationBadge } from '@/components/VerificationBadge';
import type { ApiMeta, Answer } from '@/lib/api/types';

// The backend status is live state, so this page must never be prerendered at build time.
export const dynamic = 'force-dynamic';

/**
 * Phase 1 shell.
 *
 * Its only real job right now is to prove the frontend and backend actually talk to each other, and
 * to establish the two UI conventions the product depends on: verification status is always visible,
 * and "we don't know" is rendered as an explanation rather than as a blank or a zero.
 */
export default async function Home() {
  const backend = await loadBackendMeta();

  return (
    <main className="mx-auto flex w-full max-w-3xl flex-1 flex-col gap-10 px-6 py-16">
      <header className="flex flex-col gap-3">
        <p className="text-sm font-medium uppercase tracking-widest text-blue-600 dark:text-blue-400">
          Phase 1 · Foundation
        </p>
        <h1 className="text-4xl font-semibold tracking-tight">Navi</h1>
        <p className="text-lg text-zinc-600 dark:text-zinc-400">
          Hiểu mình đang ở đâu, nên đi đâu tiếp, và vì sao.
        </p>
      </header>

      <section className="flex flex-col gap-3">
        <h2 className="text-sm font-semibold uppercase tracking-wider text-zinc-500">
          Kết nối backend
        </h2>
        <BackendStatus backend={backend} />
      </section>

      <section className="flex flex-col gap-3">
        <h2 className="text-sm font-semibold uppercase tracking-wider text-zinc-500">
          Trạng thái kiểm chứng dữ liệu
        </h2>
        <p className="text-sm text-zinc-600 dark:text-zinc-400">
          Mọi dữ liệu tri thức trong Navi đều hiển thị nguồn gốc của nó. Dữ liệu chưa kiểm chứng
          không bao giờ được trình bày như dữ liệu đã kiểm chứng.
        </p>
        <div className="flex flex-wrap gap-2">
          <VerificationBadge status="VERIFIED" />
          <VerificationBadge status="COMMUNITY" />
          <VerificationBadge status="UNVERIFIED" />
        </div>
      </section>

      <footer className="mt-auto border-t border-zinc-200 pt-6 text-sm text-zinc-500 dark:border-zinc-800">
        Chưa có tính năng nào được xây dựng. Lộ trình: <code>docs/roadmap.md</code>
      </footer>
    </main>
  );
}

function BackendStatus({ backend }: { backend: Answer<ApiMeta> }) {
  // The compiler requires both branches — the unknown case cannot be forgotten.
  if (!backend.known) {
    return (
      <div className="rounded-lg border border-amber-300 bg-amber-50 p-4 dark:border-amber-800 dark:bg-amber-950/40">
        <p className="font-medium text-amber-900 dark:text-amber-200">Chưa kết nối được</p>
        <p className="mt-1 text-sm text-amber-800 dark:text-amber-300">{backend.reason}</p>
      </div>
    );
  }

  const meta = backend.value;
  return (
    <dl className="grid grid-cols-[auto_1fr] gap-x-6 gap-y-2 rounded-lg border border-zinc-200 p-4 text-sm dark:border-zinc-800">
      <dt className="text-zinc-500">API</dt>
      <dd className="font-medium">{meta.name}</dd>
      <dt className="text-zinc-500">Version</dt>
      <dd className="font-mono">{meta.version}</dd>
      <dt className="text-zinc-500">API version</dt>
      <dd className="font-mono">{meta.apiVersion}</dd>
      <dt className="text-zinc-500">Phase</dt>
      <dd>{meta.phase}</dd>
    </dl>
  );
}

/**
 * Loads backend metadata, turning every failure into a reason a human can read.
 *
 * Note what this does not do: fall back to placeholder values. A page that invents "version 1.0.0"
 * when the backend is unreachable is the exact failure mode the Trust value forbids.
 */
async function loadBackendMeta(): Promise<Answer<ApiMeta>> {
  try {
    return { known: true, value: await apiFetch<ApiMeta>('/api/v1/meta') };
  } catch (error) {
    if (error instanceof ApiUnreachableError) {
      return {
        known: false,
        reason: `Không gọi được ${API_BASE_URL}. Kiểm tra backend đã chạy chưa — xem backend/README.md.`,
      };
    }
    if (error instanceof ApiError) {
      return {
        known: false,
        reason: `Backend trả về lỗi ${error.status}${error.code ? ` (${error.code})` : ''}: ${error.message}`,
      };
    }
    return { known: false, reason: 'Lỗi không xác định khi gọi backend.' };
  }
}
