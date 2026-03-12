import { FaRegCheckCircle } from "react-icons/fa";
import { FaRegCircleXmark } from "react-icons/fa6";
import { Card } from "@/components/ui/card";

export enum SubmissionStatus {
  ACCEPTED = "ACCEPTED",
  WRONG_ANSWER = "WRONG_ANSWER",
  QUEUED = "QUEUED",
  CANCELLED = "CANCELLED",
  COMPILED = "COMPILED",
  COMPILATION_ERROR = "COMPILATION_ERROR",
  UNSUPPORTED_LANGUAGE = "UNSUPPORTED_LANGUAGE",
}

export interface SubmissionProps {
  id: number;
  status: SubmissionStatus;
}

export const Submission = (props: SubmissionProps) => {
  return (
    <Card className="flex flex-row gap-4 p-4 items-center">
      {props.status === SubmissionStatus.ACCEPTED ? (
        <FaRegCheckCircle className="text-green-500" size="1.25rem" />
      ) : (
        <FaRegCircleXmark className="text-destructive" size="1.25rem" />
      )}
      <div className="flex flex-col gap-1">
        <p className="text-sm font-medium">Submission #{props.id}</p>
        <p className="text-sm text-muted-foreground">{props.status}</p>
      </div>
    </Card>
  );
};
