import { Spinner } from "@/shared/Spinner";
import { Submission } from "@/shared/Submission";
import { Error } from "./Error";
import { useQuery } from "@/features/QueryHook";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Badge } from "@/shared/Badge";

export const ProblemSubmissions = ({ problemId }: { problemId: number }) => {
  const { loading, submissions, error } = useQuery(
    `http://submission.localhost/api/v1/submissions/problems/${problemId}?size=${10}`,
  );

  return (

    <Card className="relative w-full h-full border border-border/60 bg-card overflow-hidden">
      <CardHeader className="px-5 flex flex-row justify-between">
        <h3 className="text-base font-semibold leading-snug tracking-tight">Testing</h3>
        <Badge title={"Accepted"} variant={"Easy"} />
      </CardHeader>
      <Separator
        className="mx-5 w-auto"
        style={{ width: "calc(100% - 2.5rem)" }}
      />
      <CardContent className="px-5 overflow-y-auto h-full">
        <div className="flex flex-col gap-4">
          <div className="flex flex-col gap-2">
            {loading ? (
              <Spinner />
            ) : submissions ? (
              submissions.submissions.content.map((submission) => (
                <Submission
                  key={"problem_submission_" + submission.submission_id}
                  id={submission.submission_id}
                  status={submission.status}
                />
              ))
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
