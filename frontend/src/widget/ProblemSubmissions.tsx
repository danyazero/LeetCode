import { Submission } from "@/shared/Submission";
import { Error } from "./Error";
import { useQuery } from "@/features/QueryHook";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Badge } from "@/shared/Badge";
import { PaginationControls } from "@/shared/PaginationControls";
import { Skeleton } from "@/components/ui/skeleton";
import { useSearchParams } from "react-router";
import { useProblemStore } from "@/features/Problem/store/useProblemStore";
import type { ProblemStatus, SubmissionsPage, ISubmission } from "@/App";
import type { SubmissionDetails } from "@/api/submissions";

export const ProblemSubmissions = ({
  problemId,
  onRestoreSubmission,
}: {
  problemId: number;
  onRestoreSubmission: (submission: SubmissionDetails) => void;
}) => {
  const [searchParams, setSearchParams] = useSearchParams();
  const page = parseInt(searchParams.get("page") || "0", 10);
  const submissionCount = useProblemStore((state) => state.submissionCount);

  const {
    loading: loadingSubmissions,
    data: submissions,
    error: errorSubmissions,
  } = useQuery<SubmissionsPage>(
    `http://submission.localhost/api/v1/problems/${problemId}?page=${page}&size=${3}&_refresh=${submissionCount}`,
  );

  const {
    loading: loadingStatus,
    data: status,
    error: errorStatus,
  } = useQuery<ProblemStatus>(
    `http://submission.localhost/api/v1/problems/${problemId}/status?_refresh=${submissionCount}`,
  );

  return (
    <Card className="relative w-full h-full border border-border/60 bg-card overflow-hidden">
      <CardHeader className="px-5 flex flex-row justify-between">
        <h3 className="text-base font-semibold leading-snug tracking-tight">
          Testing
        </h3>
        {loadingStatus ? (
          <Skeleton className="h-5 w-16 rounded-full" />
        ) : errorStatus ? null : status ? (
          <Badge
            title={status.is_solved ? "Solved" : "Unsolved"}
            variant={status.is_solved ? "Easy" : "Hard"}
          />
        ) : null}
      </CardHeader>
      <Separator
        className="mx-5 w-auto"
        style={{ width: "calc(100% - 2.5rem)" }}
      />
      <CardContent className="px-5 overflow-y-auto h-full">
        <div className="flex flex-col gap-4">
          <div className="flex flex-col gap-2">
            {loadingSubmissions ? (
              <div className="flex flex-col gap-2">
                <Skeleton className="h-[92px] w-full" />
                <Skeleton className="h-[92px] w-full" />
                <Skeleton className="h-[92px] w-full" />
              </div>
            ) : submissions ? (
              <>
                {submissions.content.map((submission: ISubmission) => (
                  <Submission
                    key={"problem_submission_" + submission.submission_id}
                    id={submission.submission_id}
                    status={submission.status}
                    onRestore={onRestoreSubmission}
                  />
                ))}
                <PaginationControls
                  currentPage={submissions.page_number}
                  totalPages={submissions.total_pages}
                  onPageChange={(nextPage) => {
                    setSearchParams({ page: nextPage.toString() });
                  }}
                />
              </>
            ) : (
              <div className="flex justify-center items-center w-full h-full">
                {errorSubmissions ? <Error error={errorSubmissions} /> : <p>Empty.</p>}
              </div>
            )}
          </div>
        </div>
      </CardContent>
    </Card>
  );
};
