import type { VerificationStatus } from '@/lib/api/types';

/**
 * Shows how far a piece of knowledge has been verified.
 *
 * This component is the visible half of the Trust core value. The backend guarantees every knowledge
 * record carries a status; this makes sure the student can see it. Rendering COMMUNITY or UNVERIFIED
 * data to look like VERIFIED data would satisfy the database constraint and still break the promise.
 */

const STYLES: Record<VerificationStatus, { label: string; className: string; title: string }> = {
  VERIFIED: {
    label: 'Đã kiểm chứng',
    className:
      'bg-emerald-50 text-emerald-700 ring-emerald-600/20 dark:bg-emerald-950 dark:text-emerald-300 dark:ring-emerald-400/20',
    title: 'Đối chiếu với nguồn chính thức',
  },
  COMMUNITY: {
    label: 'Từ cộng đồng',
    className:
      'bg-amber-50 text-amber-800 ring-amber-600/20 dark:bg-amber-950 dark:text-amber-300 dark:ring-amber-400/20',
    title: 'Do cộng đồng đóng góp và đã qua review, chưa phải nguồn chính thức',
  },
  UNVERIFIED: {
    label: 'Chưa kiểm chứng',
    className:
      'bg-rose-50 text-rose-700 ring-rose-600/20 dark:bg-rose-950 dark:text-rose-300 dark:ring-rose-400/20',
    title: 'Chưa được kiểm chứng — không nên dùng để ra quyết định',
  },
};

export function VerificationBadge({ status }: { status: VerificationStatus }) {
  const { label, className, title } = STYLES[status];

  return (
    <span
      title={title}
      className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ring-1 ring-inset ${className}`}
    >
      {label}
    </span>
  );
}
