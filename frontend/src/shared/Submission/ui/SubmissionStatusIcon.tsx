import { FaRegCheckCircle } from "react-icons/fa";
import { FaRegCircleXmark } from "react-icons/fa6";
import { 
  Loader2, 
  Clock, 
  Database, 
  Bug, 
  ServerCrash, 
  Ban, 
  HelpCircle,
  FileCode2
} from "lucide-react";

export type SubmissionStatusValue = string;

export const SubmissionStatusIcon = ({
  status,
}: {
  status: SubmissionStatusValue;
}) => {
  const s = status?.toUpperCase() || "";

  switch (s) {
    case "ACCEPTED":
      return <FaRegCheckCircle className="text-green-500" size="1.25rem" />;
    case "PARTIALLY_CORRECT":
      return <FaRegCheckCircle className="text-yellow-500" size="1.25rem" />;
    case "WRONG_ANSWER":
      return <FaRegCircleXmark className="text-destructive" size="1.25rem" />;
    case "TIME_LIMIT_EXCEEDED":
      return <Clock className="text-destructive" size="1.25rem" />;
    case "MEMORY_LIMIT_EXCEEDED":
      return <Database className="text-destructive" size="1.25rem" />;
    case "COMPILATION_ERROR":
      return <FileCode2 className="text-destructive" size="1.25rem" />;
    case "INTERNAL_ERROR":
      return <ServerCrash className="text-destructive" size="1.25rem" />;
    case "UNSUPPORTED_LANGUAGE":
      return <HelpCircle className="text-muted-foreground" size="1.25rem" />;
    case "CANCELLED":
      return <Ban className="text-muted-foreground" size="1.25rem" />;
    case "CREATED":
    case "QUEUED":
    case "RUNNING":
    case "COMPILED":
    case "CONNECTING...":
    case "CONNECTED":
    case "SUBMISSION SENT":
      return <Loader2 className="text-blue-500 animate-spin" size="1.25rem" />;
    default:
      // Fallback for unknown statuses, treated as loading if they're not explicitly known errors
      return <Loader2 className="text-muted-foreground animate-spin" size="1.25rem" />;
  }
};