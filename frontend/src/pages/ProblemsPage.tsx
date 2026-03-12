import { useState, useCallback } from "react";
import { useNavigate } from "react-router";
import { keycloakContext } from "@/features/KeycloakWrapper";
import { ProblemCard } from "@/entities/ProblemCard";
import { ProblemSearchWidget } from "@/widget/ProblemSearchWidget";
import { fetchProblems, type Problem, type ProblemSearchParams } from "@/api/problems";
import { Skeleton } from "@/components/ui/skeleton";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import type { Difficulty } from "@/shared/Badge";

export const ProblemsPage = () => {
  const navigate = useNavigate();
  const [problems, setProblems] = useState<Problem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // To handle pagination later
  // const [page, setPage] = useState(0);
  // const [hasMore, setHasMore] = useState(false);

  const [searchParams, setSearchParams] = useState<ProblemSearchParams>({ page: 0, size: 10 });

  const loadProblems = useCallback(async (params: ProblemSearchParams) => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await fetchProblems(params);
      console.log(data.content)
      setProblems(data.content);
      // For future pagination
      // setPage(data.number);
      // setHasMore(data.number < data.totalPages - 1);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load problems");
    } finally {
      setIsLoading(false);
    }
  }, []);

  const handleSearch = useCallback((params: ProblemSearchParams & { size: number }) => {
    setSearchParams(params);
    loadProblems(params);
  }, [loadProblems]);

  return (
    <div className="flex flex-row justify-center w-full ">
      <div>
        <button onClick={() => keycloakContext.login()}>Sign in</button>
        <button onClick={() => console.log(keycloakContext.token)}>
          Token
        </button>
      </div>
      <div className="flex flex-col max-w-4xl w-full gap-2 mt-8">
        <p className="text-4xl text-black">Problems</p>
        <ProblemSearchWidget onSearch={handleSearch} />

        {error && (
          <div className="p-4 text-center text-red-500 bg-red-50 rounded-lg border border-red-200 mt-4">
            <p className="text-sm font-medium">{error}</p>
            <button
              onClick={() => loadProblems(searchParams)}
              className="mt-2 text-sm text-red-600 hover:text-red-700 underline"
            >
              Try again
            </button>
          </div>
        )}

        <div className="flex flex-col gap-2 w-full mt-4">
          {isLoading ? (
            Array.from({ length: 5 }).map((_, i) => (
              <Card key={i} className="relative w-full border border-border/60 bg-card">
                <CardHeader className="px-5">
                  <div className="flex items-start gap-3">
                    <Skeleton className="h-5 w-5 rounded-full" />
                    <div className="flex flex-1 items-center gap-2 min-w-0">
                      <Skeleton className="h-5 flex-1 max-w-[250px]" />
                      <Skeleton className="h-5 w-16" />
                    </div>
                  </div>
                </CardHeader>
                <Separator className="mx-5 w-auto" style={{ width: "calc(100% - 2.5rem)" }} />
                <CardContent className="px-5">
                  <div className="flex items-center justify-between gap-4 flex-wrap">
                    <Skeleton className="h-5 w-24" />
                    <Skeleton className="h-5 w-24" />
                  </div>
                </CardContent>
              </Card>
            ))
          ) : !error && problems.length === 0 ? (
            <p className="text-center text-muted-foreground mt-8">No problems found</p>
          ) : (
            !error && problems.map((problem) => (
              <ProblemCard
                key={problem.id}
                isCompleted={false}
                id={problem.id}
                title={problem.title}
                difficulty={problem.difficulty.value as Difficulty}
                acceptance_rate={problem.acceptance_rate}
                submissions={problem.submissions}
                onClick={() => navigate(`/problem/${problem.id}`)}
              />
            ))
          )}
        </div>
      </div>
    </div>
  );
};
