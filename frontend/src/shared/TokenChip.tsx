import { cn } from "@/lib/utils";
import { AtSign, Hash, X } from "lucide-react";

export interface AppliedToken {
  kind: TokenKind;
  id: number;
  label: string;
}

export type TokenKind = "tag" | "difficulty";

export interface TokenChipProps {
  token: AppliedToken;
  onRemove: (kind: TokenKind) => void;
}

type TokenColor = Record<
  TokenKind,
  { bg: string; text: string; border: string; icon: React.ReactNode }
>;

export const TOKEN_COLORS: TokenColor = {
  tag: {
    bg: "bg-violet-50 dark:bg-violet-950/40",
    text: "text-violet-700 dark:text-violet-300",
    border: "border-violet-200 dark:border-violet-700",
    icon: <AtSign size={10} />,
  },
  difficulty: {
    bg: "bg-sky-50 dark:bg-sky-950/40",
    text: "text-sky-700 dark:text-sky-300",
    border: "border-sky-200 dark:border-sky-700",
    icon: <Hash size={10} />,
  },
};

export function TokenChip({ token, onRemove }: TokenChipProps) {
  const style = TOKEN_COLORS[token.kind];

  return (
    <span
      className={cn(
        "inline-flex items-center gap-1 rounded-md border px-2 py-0.5",
        "text-xs font-semibold leading-tight select-none",
        style.bg,
        style.text,
        style.border,
      )}
    >
      {style.icon}
      {token.label}

      <button
        type="button"
        onClick={(e) => {
          e.stopPropagation();
          onRemove(token.kind);
        }}
        className="ml-0.5 rounded-sm opacity-60 hover:opacity-100 transition-opacity"
      >
        <X size={9} />
      </button>
    </span>
  );
}
