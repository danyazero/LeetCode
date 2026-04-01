import { FaRegCheckCircle } from "react-icons/fa";
import { FaRegCircleXmark } from "react-icons/fa6";

export type SubmissionStatusValue = string;

const SUCCESS_STATUSES = new Set<SubmissionStatusValue>([
  "ACCEPTED",
  "COMPILED",
  "PARTIALLY_CORRECT",
]);

export const SubmissionStatusIcon = ({
  status,
}: {
  status: SubmissionStatusValue;
}) => {
  if (SUCCESS_STATUSES.has(status)) {
    return <FaRegCheckCircle className="text-green-500" size="1.25rem" />;
  }

  return <FaRegCircleXmark className="text-destructive" size="1.25rem" />;
};