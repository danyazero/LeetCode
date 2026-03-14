import type { Difficulty, Tag } from "@/shared/SuggestionsDropdown";
import { keycloakContext } from "@/features/KeycloakWrapper";

export type { Difficulty, Tag };

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

export interface CreateProblemDto {
  title: string;
  description: string;
  difficulty: Difficulty;
  tags: Tag[];
}

export interface TestcaseDto {
  input: string;
  expected: string;
  is_public: boolean;
  problem_id: number;
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

async function post<T>(path: string, body: any): Promise<T> {
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
  };

  if (keycloakContext.token) {
    headers["Authorization"] = `Bearer ${keycloakContext.token}`;
  }

  const res = await fetch(`${BASE_URL}${path}`, {
    method: "POST",
    headers,
    body: JSON.stringify(body),
  });
  if (!res.ok) throw new Error(`API ${res.status}: ${res.statusText}`);
  return res.json() as Promise<T>;
}

async function del(path: string): Promise<void> {
  const headers: Record<string, string> = {};

  if (keycloakContext.token) {
    headers["Authorization"] = `Bearer ${keycloakContext.token}`;
  }

  const res = await fetch(`${BASE_URL}${path}`, {
    method: "DELETE",
    headers,
  });
  if (!res.ok) throw new Error(`API ${res.status}: ${res.statusText}`);
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

export function createProblem(data: CreateProblemDto): Promise<Problem> {
  return post<Problem>("/problems", data);
}

export function createTestcase(data: TestcaseDto): Promise<any> {
  return post<any>("/testcases", data);
}

export function deleteProblem(problemId: number): Promise<void> {
  return del(`/problems/${problemId}`);
}
