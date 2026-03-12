import { Badge as UI_Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

export type Variant = "Easy" | "Medium" | "Hard";

export function Badge({ title, variant = "Easy" }: { title: string, variant?: Variant }) {
  const styles: Record<Variant, string> = {
    Easy: "bg-emerald-500/10 text-emerald-600 border-emerald-500/20 hover:bg-emerald-500/20",
    Medium:
      "bg-amber-500/10 text-amber-600 border-amber-500/20 hover:bg-amber-500/20",
    Hard: "bg-red-500/10 text-red-600 border-red-500/20 hover:bg-red-500/20",
  };


  return (
    <UI_Badge
      variant="outline"
      className={cn(
        "text-xs font-semibold tracking-wide px-2 py-0.5 transition-colors",
        styles[variant],
      )}
    >
      {title}
    </UI_Badge>
  );
}
