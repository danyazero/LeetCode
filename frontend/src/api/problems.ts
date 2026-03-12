import type { Difficulty, Tag } from "@/shared/SuggestionsDropdown";

export interface PageDto<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface Problem {
  id: number;
  title: string;
  difficulty: Difficulty;
  acceptance_rate: number;
  submissions: number;
}

export interface ProblemSearchParams {
  query?: string;
  tag?: number;
  difficulty?: number;
  page: number;
  size: number;
}

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

// All request params are optional 
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

export function fetchDifficulties(): Promise<PageDto<Difficulty>> {
  return get<PageDto<Difficulty>>("/difficulties", { page: 0, size: 10 });
}

export function fetchAllTags(page = 0, size = 20): Promise<PageDto<Tag>> {
  return get<PageDto<Tag>>("/tags", { page, size });
}

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
