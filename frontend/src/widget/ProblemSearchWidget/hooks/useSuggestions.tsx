import * as React from "react";
import {
  fetchAllTags,
  fetchTagsByQuery,
  fetchDifficulties,
} from "@/api/problems";
import { useDebounce } from "@/hooks/useDebounce";
import type { SuggestionItem } from "@/shared/SuggestionsDropdown";
import type { ActiveToken } from "./useActiveToken";

export interface UseSuggestionsReturn {
  suggestions: SuggestionItem[];
  isLoading: boolean;
  dropdownOpen: boolean;
  openDropdown: () => void;
  closeDropdown: () => void;
}

export function useSuggestions(
  activeToken: ActiveToken | null,
  setActiveIndex: (value: number) => void,
): UseSuggestionsReturn {
  const [suggestions, setSuggestions] = React.useState<SuggestionItem[]>([]);
  const [isLoading, setIsLoading] = React.useState(false);
  const [dropdownOpen, setDropdownOpen] = React.useState(false);

  const debouncedQuery = useDebounce(activeToken?.query ?? "", 300);

  React.useEffect(() => {
    if (!activeToken) {
      setSuggestions([]);
      console.log("cleared suggestions");
      setDropdownOpen(false);
      return;
    }

    let cancelled = false;
    setIsLoading(true);
    setDropdownOpen(true);
    setActiveIndex(-1);

    const request: Promise<SuggestionItem[]> =
      activeToken.kind === "tag"
        ? (debouncedQuery
            ? fetchTagsByQuery(debouncedQuery)
            : fetchAllTags()
          ).then((page) =>
            page.content.map((t) => ({ kind: "tag" as const, data: t })),
          )
        : fetchDifficulties().then((page) =>
            page.content
              .filter(
                (d) =>
                  !debouncedQuery ||
                  d.value
                    .toLowerCase()
                    .startsWith(debouncedQuery.toLowerCase()),
              )
              .map((d) => ({ kind: "difficulty" as const, data: d })),
          );

    request
      .then((items) => {
        if (!cancelled) setSuggestions(items);
      })
      .catch(console.error)
      .finally(() => {
        if (!cancelled) setIsLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [activeToken?.kind, debouncedQuery]);

  const openDropdown = React.useCallback(() => setDropdownOpen(true), []);
  const closeDropdown = React.useCallback(() => setDropdownOpen(false), []);

  return {
    suggestions,
    isLoading,
    dropdownOpen,
    openDropdown,
    closeDropdown,
  };
}
