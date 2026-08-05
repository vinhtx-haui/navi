import type { ProblemDetail } from './types';

/**
 * Minimal typed wrapper over `fetch` for talking to the Navi backend.
 *
 * Kept deliberately small: it exists to make failures explicit and typed, not to become a framework.
 * There is no business logic here — computing GPA or progress happens in the backend, once. Logic
 * living in two places is logic that will disagree with itself.
 */

const DEFAULT_BASE_URL = 'http://localhost:8080';

export const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL?.replace(/\/$/, '') ?? DEFAULT_BASE_URL;

/** An error carrying the backend's problem+json body, when the backend produced one. */
export class ApiError extends Error {
  constructor(
    readonly status: number,
    readonly problem?: ProblemDetail,
  ) {
    super(problem?.detail ?? `Request failed with status ${status}`);
    this.name = 'ApiError';
  }

  /** The stable backend error code, e.g. `course.prerequisite_not_met`. */
  get code(): string | undefined {
    return this.problem?.code;
  }
}

/** Raised when the backend could not be reached at all — distinct from an HTTP error response. */
export class ApiUnreachableError extends Error {
  constructor(cause: unknown) {
    super('Could not reach the Navi API');
    this.name = 'ApiUnreachableError';
    this.cause = cause;
  }
}

export interface RequestOptions extends Omit<RequestInit, 'body'> {
  body?: unknown;
}

export async function apiFetch<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { body, headers, ...rest } = options;

  let response: Response;
  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      ...rest,
      headers: {
        Accept: 'application/json',
        ...(body !== undefined ? { 'Content-Type': 'application/json' } : {}),
        ...headers,
      },
      body: body !== undefined ? JSON.stringify(body) : undefined,
      // Student data is per-request and must never be served from a shared cache.
      cache: rest.cache ?? 'no-store',
    });
  } catch (cause) {
    // A dead backend is a different problem from a 4xx, and the UI should say so differently.
    throw new ApiUnreachableError(cause);
  }

  if (!response.ok) {
    throw new ApiError(response.status, await readProblem(response));
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return (await response.json()) as T;
}

async function readProblem(response: Response): Promise<ProblemDetail | undefined> {
  try {
    const contentType = response.headers.get('content-type') ?? '';
    if (contentType.includes('json')) {
      return (await response.json()) as ProblemDetail;
    }
  } catch {
    // A malformed error body must not mask the status code the caller needs.
  }
  return undefined;
}
