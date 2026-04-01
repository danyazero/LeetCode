import { keycloakContext } from "@/features/KeycloakWrapper";

export interface Language {
  id: number;
  language: string;
}

export interface SubmissionEvent {
  id: number;
  status: string;
  created_at: string;
}

export interface SubmissionDetails {
  id: number;
  problem_id: number;
  status: string;
  created_at: string;
  solution: string;
  language: Language;
  events: SubmissionEvent[];
}

export interface LanguageResponse {
  last_used: number | null;
  languages: Language[];
}

const BASE_URL = "http://submission.localhost/api/v1";

export async function fetchAllLanguages(): Promise<LanguageResponse> {
  const headers: Record<string, string> = {};

  if (keycloakContext.token) {
    headers["Authorization"] = `Bearer ${keycloakContext.token}`;
  }

  const response = await fetch(`${BASE_URL}/languages`, {
    headers,
  });

  if (!response.ok) {
    throw new Error(`API ${response.status}: ${response.statusText}`);
  }

  return response.json() as Promise<LanguageResponse>;
}

export async function fetchSubmissionById(
  submissionId: number,
): Promise<SubmissionDetails> {
  const headers: Record<string, string> = {};

  if (keycloakContext.token) {
    headers["Authorization"] = `Bearer ${keycloakContext.token}`;
  }

  const response = await fetch(`${BASE_URL}/submissions/${submissionId}`, {
    headers,
  });

  if (!response.ok) {
    throw new Error(`API ${response.status}: ${response.statusText}`);
  }

  return response.json() as Promise<SubmissionDetails>;
}
