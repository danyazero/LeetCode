import * as React from "react";
import type { TokenKind } from "@/shared/TokenChip";

export interface ActiveToken {
  kind: TokenKind;
  query: string;
}

/**
 * Parses the raw input string and returns the active trigger token
 * (@<query> → tag, #<query> → difficulty) if one is present at the
 * end of the string, or null otherwise.
 */
export function useActiveToken(rawInput: string): ActiveToken | null {
  return React.useMemo<ActiveToken | null>(() => {
    const match = rawInput.match(/(?:^|[\s])(@|#)(\S*)$/);
    if (!match) return null;

    const [, prefix, query = ""] = match;
    return {
      kind: prefix === "@" ? "tag" : "difficulty",
      query,
    };
  }, [rawInput]);
}
