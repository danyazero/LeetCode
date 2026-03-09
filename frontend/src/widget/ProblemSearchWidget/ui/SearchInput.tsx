import { cn } from "@/lib/utils";
import { TokenChip, type TokenKind } from "@/shared/TokenChip";
import { Loader2, Search, X } from "lucide-react";
import type { UseTokensReturn } from "../hooks/useTokens";
import { useState, type Ref } from "react";
import type { ActiveToken } from "../hooks/useActiveToken";

export interface SearchInputProps {
  inputRef: React.RefObject<HTMLInputElement | null>;
  value: string;
  setValue: (value: string) => void;
  activeToken: ActiveToken | null;
  dropdownOpen: boolean;
  openDropdown: () => void;
  handleKeyDown: (e: React.KeyboardEvent<HTMLInputElement>) => void;
  token: UseTokensReturn;
}

export const SearchInput = (props: SearchInputProps) => {

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (!props.dropdownOpen) {
      if (
        e.key === "Backspace" &&
        props.value === "" &&
        props.token.all.length > 0
      )
        props.token.pop();
      return;
    }

    props.handleKeyDown(e);
  };

  const removeToken = (kind: TokenKind) => {
    props.token.remove(kind);
    props.inputRef.current?.focus();
  };

  const clearAll = () => {
    props.token.clear();
    props.inputRef.current?.focus();
    props.setValue("");
  };

  const hasContent = props.token.all.length > 0 || props.value.trim().length > 0;

  return (
    <div
      onClick={() => props.inputRef.current?.focus()}
      className={cn(
        "flex flex-wrap items-center gap-1.5 min-h-11 w-full",
        "rounded-xl border bg-background px-3 py-2 cursor-text",
        "transition-all duration-150",
        "border-border/70 shadow-sm hover:border-border hover:shadow",
      )}
    >
      <span className="shrink-0 text-muted-foreground/60">
        <Search size={15} />
      </span>

      {props.token.all.map((tok) => (
        <TokenChip token={tok} onRemove={(kind) => removeToken(kind)} />
      ))}

      <input
        ref={props.inputRef}
        value={props.value}
        onChange={(e) => props.setValue(e.target.value)}
        onKeyDown={handleKeyDown}
        onFocus={() => props.activeToken && props.openDropdown()}
        placeholder={
          props.token.all.length === 0
            ? "Search… use @ for tags, # for difficulty"
            : ""
        }
        className={cn(
          "flex-1 min-w-40 bg-transparent text-sm outline-none",
          "placeholder:text-muted-foreground/40",
        )}
        aria-label="Search problems"
        aria-autocomplete="list"
        aria-expanded={props.dropdownOpen}
        autoComplete="off"
        spellCheck={false}
      />

      {hasContent && (
        <button
          type="button"
          aria-label="Clear all"
          onClick={clearAll}
          className="shrink-0 rounded-md p-0.5 text-muted-foreground/50 hover:text-muted-foreground hover:bg-muted transition-all"
        >
          <X size={13} />
        </button>
      )}
    </div>
  );
};
