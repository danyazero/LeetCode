import { Send } from "lucide-react";

export function SubmissionCount({ count }: { count: number }) {
  const formatted = () => {
    return count >= 1_000_000
      ? `${(count / 1_000_000).toFixed(1)}M`
      : count >= 1_000
        ? `${(count / 1_000).toFixed(1)}K`
        : count.toString();
  }


  return (
    <div className="flex items-center gap-1.5 text-xs text-muted-foreground whitespace-nowrap">
      <Send size={12} className="shrink-0" />
      <span className="font-medium text-foreground">{formatted()}</span>
      <span className="hidden sm:inline">submissions</span>
    </div>
  );
}
