import { useState } from "react";
import { BookOpenText, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { fetchSubmissionById, type SubmissionDetails } from "@/api/submissions";
import type { SubmissionStatus } from "@/shared/Submission";
import { sortSubmissionEvents } from "../utils/sortSubmissionEvents";
import { SubmissionStatusIcon } from "./SubmissionStatusIcon";
import { formatSubmissionStatus } from "../utils/formatSubmissionStatus";
import { SubmissionHistoryDialog } from "./SubmissionHistoryDialog";

export interface SubmissionProps {
  id: number;
  status: SubmissionStatus;
  onRestore: (submission: SubmissionDetails) => void;
}

export const formatSubmissionTimestamp = (value: string) =>
  new Date(value).toLocaleString();

export const Submission = ({ id, status, onRestore }: SubmissionProps) => {
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
      const response = await fetchSubmissionById(id);
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

    onRestore(response);
  };

  const handleHistoryOpenChange = (open: boolean) => {
    setIsHistoryOpen(open);

    if (!open || details || isHistoryLoading) {
      return;
    }

    void loadDetails("history");
  };

  const history = details?.events ? sortSubmissionEvents(details.events) : [];

  return (
    <Card className="p-4">
      <div className="flex items-start justify-between gap-4">
        <div className="flex min-w-0 items-center gap-4">
          <SubmissionStatusIcon status={status} />
          <div className="flex min-w-0 flex-col gap-1">
            <p className="text-sm font-medium">Submission #{id}</p>
            <p className="text-sm text-muted-foreground">
              {formatSubmissionStatus(status)}
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

          <SubmissionHistoryDialog
            detailsError={detailsError}
            events={history}
            isDisabled={isRestoring}
            isLoading={isHistoryLoading}
            isOpen={isHistoryOpen}
            submissionId={id}
            onOpenChange={handleHistoryOpenChange}
          />
        </div>
      </div>
    </Card>
  );
};
