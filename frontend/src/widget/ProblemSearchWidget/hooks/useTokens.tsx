import * as React from "react";
import type { AppliedToken, TokenKind } from "@/shared/TokenChip";
import type { SuggestionItem } from "@/shared/SuggestionsDropdown";
import { useSearchStore } from "../store/useSearchStore";

export interface UseTokensReturn {
  all: AppliedToken[];
  set: (item: SuggestionItem) => void;
  remove: (kind: TokenKind) => void;
  pop: () => void;
  clear: () => void;
}

export function useTokens(): UseTokensReturn {
  const storeTokens = useSearchStore((state) => state.tokens);
  const addToken = useSearchStore((state) => state.addToken);
  const removeToken = useSearchStore((state) => state.removeToken);
  const clearTokens = useSearchStore((state) => state.clearTokens);

  const tokens = React.useMemo<AppliedToken[]>(() => {
    return storeTokens.map(t => ({
      kind: t.kind,
      id: t.data.id,
      label: t.data.value
    }));
  }, [storeTokens]);

  const set = React.useCallback((item: SuggestionItem) => {
    addToken(item);
  }, [addToken]);

  const remove = React.useCallback((kind: TokenKind) => {
    const tokenToRemove = storeTokens.find(t => t.kind === kind);
    if (tokenToRemove) {
      removeToken(tokenToRemove.data.id.toString(), kind);
    }
  }, [storeTokens, removeToken]);

  const clear = React.useCallback(() => {
    clearTokens();
  }, [clearTokens]);

  const pop = React.useCallback(() => {
    if (storeTokens.length > 0) {
      const lastToken = storeTokens[storeTokens.length - 1];
      if (lastToken) {
        removeToken(lastToken.data.id.toString(), lastToken.kind);
      }
    }
  }, [storeTokens, removeToken]);

  return { all: tokens, set, remove, pop, clear };
}
