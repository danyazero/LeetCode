import * as React from "react";
import { Loader2, Hash, AtSign } from "lucide-react";
import { cn } from "@/lib/utils";
import { useDebounce } from "@/hooks/useDebounce";
import type { ProblemSearchParams } from "@/api/problems";
import {
  SuggestionsDropdown,
  type SuggestionItem,
} from "@/shared/SuggestionsDropdown";
import { useTokens } from "../hooks/useTokens";
import { useActiveToken } from "../hooks/useActiveToken";
import { useSuggestions } from "../hooks/useSuggestions";
import { SearchInput } from "./SearchInput";
import { useSearchStore } from "../store/useSearchStore";

interface ProblemSearchWidgetProps {
  onSearch: (params: ProblemSearchParams & { size: number }) => void;
  defaultSize?: number;
}

export function ProblemSearchWidget({
  onSearch,
  defaultSize = 10,
}: ProblemSearchWidgetProps) {
  const inputRef = React.useRef<HTMLInputElement>(null);
  const containerRef = React.useRef<HTMLDivElement>(null);

  const rawInput = useSearchStore((state) => state.rawInput);
  const setRawInput = useSearchStore((state) => state.setRawInput);
  const activeIndex = useSearchStore((state) => state.activeIndex);
  const setActiveIndex = useSearchStore((state) => state.setActiveIndex);
  const dropdownOpen = useSearchStore((state) => state.dropdownOpen);
  const setDropdownOpen = useSearchStore((state) => state.setDropdownOpen);
  const applySuggestion = useSearchStore((state) => state.applySuggestion);

  const token = useTokens();
  const activeToken = useActiveToken(rawInput);

  const {
    suggestions,
    isLoading: sugLoading,
  } = useSuggestions(activeToken);

  const plainQuery = React.useMemo(() => {
    return rawInput.replace(/(?:^|[\s])(?:@|#)\S*/g, "").trim();
  }, [rawInput]);

  const debouncedPlain = useDebounce(plainQuery, 400);

  React.useEffect(() => {
    const tagToken = token.all.find((t) => t.kind === "tag");
    const diffToken = token.all.find((t) => t.kind === "difficulty");
    onSearch({
      query: debouncedPlain || undefined,
      tag: tagToken?.id,
      difficulty: diffToken?.id,
      page: 0,
      size: defaultSize,
    });
  }, [debouncedPlain, token.all]);

  const applyToken = (item: SuggestionItem) => {
    applySuggestion(item, activeToken?.kind ?? null);
    inputRef.current?.focus();
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "ArrowDown") {
      e.preventDefault();
      setActiveIndex((i) => Math.min(i + 1, suggestions.length - 1));
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setActiveIndex((i) => Math.max(i - 1, 0));
    } else if (e.key === "Enter" && activeIndex >= 0) {
      e.preventDefault();
      if (suggestions[activeIndex]) {
        applyToken(suggestions[activeIndex]);
      }
    } else if (e.key === "Escape") {
      setDropdownOpen(false);
    }
  };

  React.useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (
        containerRef.current &&
        !containerRef.current.contains(e.target as Node)
      ) {
        setDropdownOpen(false);
      }
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  return (
    <div ref={containerRef} className="relative w-full">
      <SearchInput
        inputRef={inputRef}
        value={rawInput}
        setValue={setRawInput}
        activeToken={activeToken}
        dropdownOpen={dropdownOpen}
        openDropdown={() => setDropdownOpen(true)}
        handleKeyDown={handleKeyDown}
        token={token}
      />
      {dropdownOpen && (
        <div
          className={cn(
            "absolute left-0 right-0 top-[calc(100%+6px)] z-50",
            "rounded-xl border border-border/80 bg-popover shadow-xl shadow-black/8",
            "overflow-hidden",
          )}
        >
          <div className="flex items-center gap-2 px-3 py-2 border-b border-border/50 bg-muted/40">
            {activeToken?.kind === "tag" ? (
              <>
                <AtSign size={12} className="text-violet-500" />
                <span className="text-xs font-medium text-muted-foreground">
                  Tags
                </span>
              </>
            ) : (
              <>
                <Hash size={12} className="text-sky-500" />
                <span className="text-xs font-medium text-muted-foreground">
                  Difficulty levels
                </span>
              </>
            )}

            {sugLoading && (
              <Loader2
                size={11}
                className="animate-spin text-muted-foreground ml-auto"
              />
            )}
          </div>

          <SuggestionsDropdown
            suggestions={suggestions}
            activeIndex={activeIndex}
            onHover={(idx) => setActiveIndex(idx)}
            onSelect={(it) => applyToken(it)}
            loading={sugLoading}
          />
        </div>
      )}
    </div>
  );
}
