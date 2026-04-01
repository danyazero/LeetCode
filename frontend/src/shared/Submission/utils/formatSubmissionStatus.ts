
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

export const formatSubmissionStatus = (status: string) =>
  status
    .toLowerCase()
    .split("_")
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");