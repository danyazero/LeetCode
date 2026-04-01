import { History } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Skeleton } from "@/components/ui/skeleton";
import type { SubmissionEvent } from "@/api/submissions";
import { formatSubmissionStatus } from "../utils/formatSubmissionStatus";
import { formatSubmissionTimestamp } from "./Submission";

interface SubmissionHistoryDialogProps {
  detailsError?: string;
  events: SubmissionEvent[];
  isDisabled: boolean;
  isLoading: boolean;
  isOpen: boolean;
  submissionId: number;
  onOpenChange: (open: boolean) => void;
}

export const SubmissionHistoryDialog = ({
  detailsError,
  events,
  isDisabled,
  isLoading,
  isOpen,
  submissionId,
  onOpenChange,
}: SubmissionHistoryDialogProps) => {
  return (
    <Dialog open={isOpen} onOpenChange={onOpenChange}>
      <DialogTrigger asChild>
        <Button variant="outline" size="sm" disabled={isDisabled}>
          <History />
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-xl">
        <DialogHeader>
          <DialogTitle>Submission #{submissionId} history</DialogTitle>
          <DialogDescription>
            Status transitions for this submission.
          </DialogDescription>
        </DialogHeader>

        <div className="max-h-104 overflow-y-auto pr-1">
          {isLoading ? (
            <div className="flex flex-col gap-3">
              <Skeleton className="h-14 w-full" />
              <Skeleton className="h-14 w-full" />
              <Skeleton className="h-14 w-full" />
            </div>
          ) : detailsError ? (
            <p className="text-sm text-destructive">{detailsError}</p>
          ) : events.length > 0 ? (
            <div className="flex flex-col gap-3">
              {events.map((event) => (
                <div
                  key={event.id}
                  className="rounded-lg border border-border/60 bg-muted/30 px-3 py-2"
                >
                  <p className="text-sm font-medium">
                    {formatSubmissionStatus(event.status)}
                  </p>
                  <p className="text-xs text-muted-foreground">
                    {formatSubmissionTimestamp(event.created_at)}
                  </p>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-sm text-muted-foreground">
              No history events found.
            </p>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
};
