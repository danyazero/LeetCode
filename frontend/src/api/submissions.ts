import { keycloakContext } from "@/features/KeycloakWrapper";

export interface Language {
  id: number;
  language: string;
}

export interface LanguageResponse {
  last_used: number;
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
