import { Submission } from "@/shared/Submission";
import { Error } from "./Error";
import { useQuery } from "@/features/QueryHook";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Badge } from "@/shared/Badge";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
import { useSearchParams } from "react-router";
import { useProblemStore } from "@/features/Problem/store/useProblemStore";

export const ProblemSubmissions = ({ problemId }: { problemId: number }) => {
  const [searchParams, setSearchParams] = useSearchParams();
  const page = parseInt(searchParams.get("page") || "0", 10);
  const submissionCount = useProblemStore((state) => state.submissionCount);

  const { loading, submissions, error } = useQuery(
    `http://submission.localhost/api/v1/submissions/problems/${problemId}?page=${page}&size=${3}&_refresh=${submissionCount}`,
  );


  return (

    <Card className="relative w-full h-full border border-border/60 bg-card overflow-hidden">
      <CardHeader className="px-5 flex flex-row justify-between">
        <h3 className="text-base font-semibold leading-snug tracking-tight">Testing</h3>
        {loading ? (
          <Skeleton className="h-5 w-16 rounded-full" />
        ) : error ? null : submissions ? (
          <Badge 
            title={submissions.is_accepted ? "Solved" : "Unsolved"} 
            variant={submissions.is_accepted ? "Easy" : "Hard"} 
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
            {loading ? (
              <div className="flex flex-col gap-2">
                <Skeleton className="h-[68px] w-full" />
                <Skeleton className="h-[68px] w-full" />
                <Skeleton className="h-[68px] w-full" />
              </div>
            ) : submissions ? (
              <>
                {submissions.submissions.content.map((submission: any) => (
                  <Submission
                    key={"problem_submission_" + submission.submission_id}
                    id={submission.submission_id}
                    status={submission.status}
                  />
                ))}
                {submissions.submissions.total_pages > 1 && (
                  <Pagination className="mt-4">
                    <PaginationContent>
                      <PaginationItem>
                        <PaginationPrevious
                          onClick={() => {
                            if (!submissions.submissions.is_first) {
                              setSearchParams({ page: (page - 1).toString() });
                            }
                          }}
                          className={submissions.submissions.is_first ? "pointer-events-none opacity-50" : "cursor-pointer"}
                        />
                      </PaginationItem>
                      <PaginationItem>
                        <span className="text-sm text-muted-foreground mx-4">
                          Page {submissions.submissions.page_number + 1} of {submissions.submissions.total_pages}
                        </span>
                      </PaginationItem>
                      <PaginationItem>
                        <PaginationNext
                          onClick={() => {
                            if (!submissions.submissions.is_last) {
                              setSearchParams({ page: (page + 1).toString() });
                            }
                          }}
                          className={submissions.submissions.is_last ? "pointer-events-none opacity-50" : "cursor-pointer"}
                        />
                      </PaginationItem>
                    </PaginationContent>
                  </Pagination>
                )}
              </>
            ) : (
              <div className="flex justify-center items-center w-full h-full">
                {error ? <Error error={error} /> : <p>Empty.</p>}
              </div>
            )}
          </div>
        </div>
      </CardContent>
    </Card>

  );
};
