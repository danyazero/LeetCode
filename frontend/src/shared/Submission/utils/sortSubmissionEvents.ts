import type { SubmissionEvent } from "@/api/submissions";

export const sortSubmissionEvents = (events: SubmissionEvent[]) =>
  [...events].sort(
    (left, right) =>
      new Date(left.created_at).getTime() - new Date(right.created_at).getTime(),
  );