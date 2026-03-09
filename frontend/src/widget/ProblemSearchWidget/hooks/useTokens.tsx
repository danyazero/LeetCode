import * as React from "react";
import type { AppliedToken, TokenKind } from "@/shared/TokenChip";
import type { SuggestionItem } from "@/shared/SuggestionsDropdown";

export interface UseTokensReturn {
  all: AppliedToken[];
  set: (item: SuggestionItem) => void;
  remove: (kind: TokenKind) => void;
  pop: () => void;

  clear: () => void;
}

/**
 * Manages the list of applied filter tokens (tag / difficulty).
 * Only one token per kind is allowed; applying a new one of the same
 * kind replaces the previous one.
 */
export function useTokens(): UseTokensReturn {
  const [tokens, setTokens] = React.useState<AppliedToken[]>([]);

  const set = React.useCallback((item: SuggestionItem) => {
    const { kind, data } = item;
    setTokens((prev) => [
      ...prev.filter((t) => t.kind !== kind),
      { kind, id: data.id, label: data.value },
    ]);
  }, []);

  const remove = React.useCallback((kind: TokenKind) => {
    setTokens((prev) => prev.filter((t) => t.kind !== kind));
  }, []);

  const clear = React.useCallback(() => {
    setTokens([]);
  }, []);

  const pop = React.useCallback(() => {
    setTokens((prev) => prev.slice(0, -1));
  }, []);
  
  return { all: tokens, set, remove, pop, clear };
}
