import { cn } from "@/lib/utils";
import { TOKEN_COLORS } from "./TokenChip";

export interface Tag {
  id: number;
  value: string;
}

export interface Difficulty {
  id: number;
  value: string;
}

export type SuggestionItem =
  | { kind: "tag"; data: Tag }
  | { kind: "difficulty"; data: Difficulty };

interface SuggestionDropdownProps {
  suggestions: SuggestionItem[];
  activeIndex: number;
  loading: boolean;
  onHover: (index: number) => void;
  onSelect: (item: SuggestionItem) => void;
}

export function SuggestionsDropdown({
  suggestions,
  activeIndex,
  loading,
  onHover,
  onSelect,
}: SuggestionDropdownProps) {
  return (
    <div className="max-h-56 overflow-y-auto py-1">
      {suggestions.length === 0 && !loading ? (
        <div className="py-6 text-center text-xs text-muted-foreground">
          No results found.
        </div>
      ) : (
        suggestions.map((item: SuggestionItem, idx: number) => {
          const label = item.kind === "tag" ? item.data.value : item.data.value;

          const color = TOKEN_COLORS[item.kind];

          return (
            <button
              key={`${item.kind}-${item.data.id}`}
              onMouseDown={(e) => {
                e.preventDefault();
                onSelect(item);
              }}
              onMouseEnter={() => onHover(idx)}
              className={cn(
                "w-full flex items-center gap-2.5 px-3 py-2 text-sm text-left",
                "transition-colors duration-75",
                idx === activeIndex ? "bg-muted" : "hover:bg-muted/60",
              )}
            >
              <span
                className={cn(
                  "flex items-center justify-center w-5 h-5 rounded-md border text-[10px]",
                  color.bg,
                  color.text,
                  color.border,
                )}
              >
                {color.icon}
              </span>

              <span className="font-medium">{label}</span>
            </button>
          );
        })
      )}
    </div>
  );
}
