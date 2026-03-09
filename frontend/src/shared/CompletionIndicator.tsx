import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { CheckCircle2, Circle } from "lucide-react";

export function CompletionIndicator({ isCompleted }: { isCompleted: boolean }) {
  return (
    <TooltipProvider delayDuration={150}>
      <Tooltip>
        <TooltipTrigger asChild>
          <span className="shrink-0">
            {isCompleted ? (
              <CheckCircle2
                size={20}
                className="text-emerald-500"
                aria-label="Solved"
              />
            ) : (
              <Circle
                size={20}
                className="text-muted-foreground/40"
                aria-label="Not solved"
              />
            )}
          </span>
        </TooltipTrigger>
        <TooltipContent side="right" className="text-xs">
          {isCompleted ? "Solved" : "Not solved"}
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
}
