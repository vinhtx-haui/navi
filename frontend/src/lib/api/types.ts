/**
 * Types mirroring the backend API contract.
 *
 * These are hand-written only while the backend has no OpenAPI document. Once springdoc is wired up
 * (Phase 1, with the identity module), this file is replaced by generated types — one contract, one
 * source of truth. Hand-maintained duplicates drift, and a frontend that disagrees with the backend
 * about a shape is a bug waiting for a deploy.
 */

/** GET /api/v1/meta */
export interface ApiMeta {
  name: string;
  version: string;
  apiVersion: string;
  phase: string;
}

/**
 * RFC 9457 problem detail, as produced by the backend's ApiExceptionHandler.
 *
 * `code` is the stable, machine-readable discriminator — branch on it rather than on `detail`,
 * which is human-facing text and free to change or be translated.
 */
export interface ProblemDetail {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance?: string;
  code?: string;
  fieldErrors?: Record<string, string>;
}

/**
 * How far a piece of knowledge has been verified. Mirrors the backend enum.
 *
 * The UI must never render COMMUNITY or UNVERIFIED data to look like VERIFIED data — that is a
 * product requirement, not a styling preference. See docs/vision.md §4.2.
 */
export type VerificationStatus = 'VERIFIED' | 'COMMUNITY' | 'UNVERIFIED';

export interface Provenance {
  sourceId: string;
  status: VerificationStatus;
  verifiedAt?: string;
  verifiedBy?: string;
}

/**
 * A value the backend either knows or explicitly does not.
 *
 * Mirrors the sealed `Answer<T>` type in the backend's shared kernel. The discriminated union means
 * TypeScript forces the unknown case to be handled, the same way the sealed interface does in Java —
 * so "no data" cannot silently render as an empty box or a zero.
 */
export type Answer<T> =
  | { known: true; value: T }
  | { known: false; reason: string };
