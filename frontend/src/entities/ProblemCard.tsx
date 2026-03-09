import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { cn } from "@/lib/utils";
import { DifficultyBadge, type Difficulty } from "@/shared/DifficultyBadge";
import { CompletionIndicator } from "@/shared/CompletionIndicator";
import { AcceptanceRate } from "@/shared/AcceptanceRate";
import { SubmissionCount } from "@/shared/SubmissionCount";

export interface ProblemCardProps {
  id: number;
  title: string;
  difficulty: Difficulty;
  acceptanceRate: number; // 0–100
  submissionsCount: number;
  isCompleted: boolean;
  onClick?: () => void;
}

export function ProblemCard({
  id,
  title,
  difficulty,
  acceptanceRate,
  submissionsCount,
  isCompleted,
  onClick,
}: ProblemCardProps) {
  return (
    <Card
      onClick={onClick}
      className={cn(
        "relative w-full transition-all duration-200",
        "border border-border/60 bg-card",
        "hover:border-border hover:shadow-md hover:shadow-black/5",
        "active:scale-[0.995]",
        onClick && "cursor-pointer",
      )}
    >
      <CardHeader className="px-5">
        <div className="flex items-start gap-3">
          <CompletionIndicator isCompleted={isCompleted} />

          <div className="flex flex-1 items-center gap-2 min-w-0">
            <h3
              className={cn(
                "flex-1 truncate text-sm font-semibold leading-snug tracking-tight",
                "group-hover:text-primary transition-colors duration-150",
                isCompleted &&
                  "text-muted-foreground line-through decoration-muted-foreground/40",
              )}
              title={title}
            >
              {title}
            </h3>
            <DifficultyBadge difficulty={difficulty} />
          </div>
        </div>
      </CardHeader>

      <Separator
        className="mx-5 w-auto"
        style={{ width: "calc(100% - 2.5rem)" }}
      />

      <CardContent className="px-5">
        <div className="flex items-center justify-between gap-4 flex-wrap">
          <AcceptanceRate rate={acceptanceRate} />
          <SubmissionCount count={submissionsCount} />
        </div>
      </CardContent>
    </Card>
  );
}

export default ProblemCard;
