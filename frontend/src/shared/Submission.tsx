import { FaRegCheckCircle } from "react-icons/fa";
import { FaRegCircleXmark } from "react-icons/fa6";

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
    <div className="flex flex-row gap-4 bg-foreground rounded-lg p-4 items-center">
      {props.status === SubmissionStatus.ACCEPTED ? (
        <FaRegCheckCircle color="var(--color-green)" size="1.25rem" />
      ) : (
        <FaRegCircleXmark color="var(--color-red-500)" size="1.25rem" />
      )}
      <div className="flex flex-col gap-1">
        <p className="text-sm font-medium">Submission #{props.id}</p>
        <p className="text-sm text-gray">{props.status}</p>
      </div>
    </div>
  );
};
