import { cn } from "@/lib/utils";
import { TrendingUp } from "lucide-react";

export function AcceptanceRate({ rate }: { rate: number }) {
  const clampedRate = Math.min(100, Math.max(0, rate));

  const barColor =
    clampedRate >= 60
      ? "bg-emerald-500"
      : clampedRate >= 35
        ? "bg-amber-500"
        : "bg-red-500";

  return (
    <div className="flex flex-col gap-1 min-w-[90px]">
      <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
        <TrendingUp size={12} className="shrink-0" />
        <span className="font-medium text-foreground">
          {clampedRate.toFixed(1)}%
        </span>
        <span className="hidden sm:inline">accepted</span>
      </div>
      <div className="h-1.5 w-full rounded-full bg-muted overflow-hidden">
        <div
          className={cn(
            "h-full rounded-full transition-all duration-500",
            barColor,
          )}
          style={{ width: `${clampedRate}%` }}
          role="progressbar"
          aria-valuenow={clampedRate}
          aria-valuemin={0}
          aria-valuemax={100}
        />
      </div>
    </div>
  );
}
