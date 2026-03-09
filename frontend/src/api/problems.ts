import type { Difficulty, Tag } from "@/shared/SuggestionsDropdown";

export interface PageDto<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // current page (0-indexed)
  size: number;
}

// ─── Domain entities ──────────────────────────────────────────────────────────

export interface Problem {
  id: number;
  title: string;
  difficulty: Difficulty;
  acceptanceRate: number;
  submissionsCount: number;
  isCompleted: boolean;
  tags: Tag[];
}

// ─── Search params (mirrors Spring @RequestParams) ────────────────────────────

export interface ProblemSearchParams {
  query?: string;
  tag?: number;
  difficulty?: number;
  page: number;
  size: number;
}

// ─── Base config ──────────────────────────────────────────────────────────────

const BASE_URL = "http://problem.localhost/api/v1";

async function get<T>(
  path: string,
  params?: Record<string, string | number | undefined>,
): Promise<T> {
  const url = new URL(`${BASE_URL}${path}`);

  if (params) {
    Object.entries(params).forEach(([k, v]) => {
      if (v !== undefined && v !== "") url.searchParams.set(k, String(v));
    });
  }

  const res = await fetch(url.toString());
  if (!res.ok) throw new Error(`API ${res.status}: ${res.statusText}`);
  return res.json() as Promise<T>;
}

// ─── Problems ─────────────────────────────────────────────────────────────────

/**
 * GET /problems
 * Maps to: getProblems(@RequestParam query, tag, difficulty, page, size)
 */
export function fetchProblems(
  params: ProblemSearchParams,
): Promise<PageDto<Problem>> {
  return get<PageDto<Problem>>("/problems", {
    query: params.query,
    tag: params.tag,
    difficulty: params.difficulty,
    page: params.page,
    size: params.size,
  });
}

// ─── Difficulties ─────────────────────────────────────────────────────────────

/**
 * GET /difficulties
 * Maps to: findAll(@RequestParam page, size)
 * Fetches the full first page (size=100) to populate the filter dropdown.
 */
export function fetchDifficulties(): Promise<PageDto<Difficulty>> {
  return get<PageDto<Difficulty>>("/difficulties", { page: 0, size: 10 });
}

// ─── Tags ─────────────────────────────────────────────────────────────────────

/**
 * GET /tags  — all tags (initial load / empty search query)
 * Maps to: findAll(@RequestParam page, size)
 */
export function fetchAllTags(page = 0, size = 20): Promise<PageDto<Tag>> {
  return get<PageDto<Tag>>("/tags", { page, size });
}

/**
 * GET /tags/{query}  — filtered by query string
 * Maps to: getTagsByQuery(@PathVariable query, @RequestParam page, size)
 */
export function fetchTagsByQuery(
  query: string,
  page = 0,
  size = 20,
): Promise<PageDto<Tag>> {
  return get<PageDto<Tag>>(`/tags/${encodeURIComponent(query)}`, {
    page,
    size,
  });
}
