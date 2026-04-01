import { useState } from "react";
import { BookOpenText, History, Loader2 } from "lucide-react";
import { FaRegCheckCircle } from "react-icons/fa";
import { FaRegCircleXmark } from "react-icons/fa6";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Skeleton } from "@/components/ui/skeleton";
import {
  fetchSubmissionById,
  type SubmissionDetails,
  type SubmissionEvent,
} from "@/api/submissions";

export enum SubmissionStatus {
  CREATED = "CREATED",
  ACCEPTED = "ACCEPTED",
  WRONG_ANSWER = "WRONG_ANSWER",
  QUEUED = "QUEUED",
  RUNNING = "RUNNING",
  CANCELLED = "CANCELLED",
  COMPILED = "COMPILED",
  COMPILATION_ERROR = "COMPILATION_ERROR",
  UNSUPPORTED_LANGUAGE = "UNSUPPORTED_LANGUAGE",
  TIME_LIMIT_EXCEEDED = "TIME_LIMIT_EXCEEDED",
  MEMORY_LIMIT_EXCEEDED = "MEMORY_LIMIT_EXCEEDED",
  PARTIALLY_CORRECT = "PARTIALLY_CORRECT",
  INTERNAL_ERROR = "INTERNAL_ERROR",
}

export interface SubmissionProps {
  id: number;
  status: SubmissionStatus;
  onRestore: (submission: SubmissionDetails) => void;
}

const SUCCESS_STATUSES = new Set<SubmissionStatus>([
  SubmissionStatus.ACCEPTED,
  SubmissionStatus.COMPILED,
  SubmissionStatus.PARTIALLY_CORRECT,
]);

const formatStatus = (status: string) =>
  status
    .toLowerCase()
    .split("_")
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");

const formatTimestamp = (value: string) => new Date(value).toLocaleString();

const sortEvents = (events: SubmissionEvent[]) =>
  [...events].sort(
    (left, right) =>
      new Date(left.created_at).getTime() - new Date(right.created_at).getTime(),
  );

export const Submission = (props: SubmissionProps) => {
  const [details, setDetails] = useState<SubmissionDetails>();
  const [detailsError, setDetailsError] = useState<string>();
  const [isRestoring, setIsRestoring] = useState(false);
  const [isHistoryOpen, setIsHistoryOpen] = useState(false);
  const [isHistoryLoading, setIsHistoryLoading] = useState(false);

  const loadDetails = async (mode: "restore" | "history") => {
    if (details) {
      return details;
    }

    setDetailsError(undefined);

    if (mode === "restore") {
      setIsRestoring(true);
    } else {
      setIsHistoryLoading(true);
    }

    try {
      const response = await fetchSubmissionById(props.id);
      setDetails(response);
      return response;
    } catch (error) {
      console.error("Failed to load submission details:", error);
      setDetailsError("Failed to load submission details.");
      return undefined;
    } finally {
      if (mode === "restore") {
        setIsRestoring(false);
      } else {
        setIsHistoryLoading(false);
      }
    }
  };

  const handleRestore = async () => {
    const response = await loadDetails("restore");

    if (!response) {
      return;
    }

    props.onRestore(response);
  };

  const handleHistoryOpenChange = (open: boolean) => {
    setIsHistoryOpen(open);

    if (!open || details || isHistoryLoading) {
      return;
    }

    void loadDetails("history");
  };

  const history = details?.events ? sortEvents(details.events) : [];

  return (
    <Card className="p-4">
      <div className="flex items-start justify-between gap-4">
        <div className="flex min-w-0 items-center gap-4">
          {SUCCESS_STATUSES.has(props.status) ? (
            <FaRegCheckCircle className="text-green-500" size="1.25rem" />
          ) : (
            <FaRegCircleXmark className="text-destructive" size="1.25rem" />
          )}
          <div className="flex min-w-0 flex-col gap-1">
            <p className="text-sm font-medium">Submission #{props.id}</p>
            <p className="text-sm text-muted-foreground">
              {formatStatus(props.status)}
            </p>
            {detailsError ? (
              <p className="text-xs text-destructive">{detailsError}</p>
            ) : null}
          </div>
        </div>

        <div className="flex shrink-0 items-center gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={handleRestore}
            disabled={isRestoring || isHistoryLoading}
          >
            {isRestoring ? (
              <Loader2 className="animate-spin" />
            ) : (
              <BookOpenText />
            )}
          </Button>

          <Dialog open={isHistoryOpen} onOpenChange={handleHistoryOpenChange}>
            <DialogTrigger asChild>
              <Button variant="outline" size="sm" disabled={isRestoring}>
                <History />
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-xl">
              <DialogHeader>
                <DialogTitle>Submission #{props.id} history</DialogTitle>
                <DialogDescription>
                  Status transitions for this submission.
                </DialogDescription>
              </DialogHeader>

              <div className="max-h-104 overflow-y-auto pr-1">
                {isHistoryLoading ? (
                  <div className="flex flex-col gap-3">
                    <Skeleton className="h-14 w-full" />
                    <Skeleton className="h-14 w-full" />
                    <Skeleton className="h-14 w-full" />
                  </div>
                ) : detailsError ? (
                  <p className="text-sm text-destructive">{detailsError}</p>
                ) : history.length > 0 ? (
                  <div className="flex flex-col gap-3">
                    {history.map((event) => (
                      <div
                        key={event.id}
                        className="rounded-lg border border-border/60 bg-muted/30 px-3 py-2"
                      >
                        <p className="text-sm font-medium">
                          {formatStatus(event.status)}
                        </p>
                        <p className="text-xs text-muted-foreground">
                          {formatTimestamp(event.created_at)}
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
        </div>
      </div>
    </Card>
  );
};
