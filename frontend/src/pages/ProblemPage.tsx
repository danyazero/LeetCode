import type { SubmissionsResponse } from "@/App";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Badge, type Variant } from "@/shared/Badge";
import { ProblemTag } from "@/shared/ProblemTag";
import { Example } from "@/widget/Example";
import { ProblemSubmissions } from "@/widget/ProblemSubmissions";
import { Link, useLoaderData } from "react-router";
import { Editor } from "@/widget/Editor";
import { useEffect } from "react";
import { ArrowLeft, Play, Loader2 } from "lucide-react";
import { keycloakContext } from "@/features/KeycloakWrapper";
import { UserAvatar } from "@/shared/UserAvatar";
import { useProblemStore } from "@/features/Problem/store/useProblemStore";
import { cn } from "@/lib/utils";

export interface IProblem {
  id: number;
  title: string;
  description: string;
  difficulty: IDifficulty;
  testcases: ITestcase[];
}

export interface IDifficulty {
  id: number;
  value: string;
}

export interface ITestcase {
  id: number;
  input: string;
  expected: string;
}

export interface ProblemData {
  problem: IProblem;
  submissions: SubmissionsResponse | null;
}

export const ProblemPage = () => {
  const data = useLoaderData<IProblem>();
  const submitSolution = useProblemStore((state) => state.submitSolution);
  const isSubmitting = useProblemStore((state) => state.isSubmitting);
  const setCode = useProblemStore((state) => state.setCode);

  useEffect(() => {
    setCode(""); // Clear code on mount
  }, [setCode, data?.id]);

  if (!data) {
    return <div>Loading...</div>;
  }

  return (
    <div className="flex flex-col px-6 h-screen">
      <div className="flex flex-row py-5 gap-4 items-center justify-between">
        <div className="flex flex-row gap-4 items-center">
          <Link to="/" className="hover:cursor-pointer text-muted-foreground hover:text-foreground transition-colors">
            <ArrowLeft size="1.25rem" />
          </Link>
          <ProblemTag id={data.id} isCompleted={true} />
          <p className="text-base font-medium">{data.title}</p>
        </div>
        <div className="flex items-center gap-4">
          {keycloakContext.authenticated ? (
            <>
              <UserAvatar username={keycloakContext.idTokenParsed?.preferred_username} />
              <button
                onClick={() => keycloakContext.logout()}
                className="text-sm font-medium hover:cursor-pointer hover:text-muted-foreground transition-colors"
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <button
                onClick={() => keycloakContext.login()}
                className="text-sm font-medium hover:cursor-pointer hover:text-muted-foreground transition-colors"
              >
                Sign in
              </button>
              <button
                onClick={() => keycloakContext.register()}
                className="text-sm font-medium bg-primary text-primary-foreground px-4 py-2 rounded-md hover:bg-primary/90 transition-colors"
              >
                Sign up
              </button>
            </>
          )}
        </div>
      </div>
      <div className="flex flex-row gap-2 w-full h-full relative mb-4 mr-4">
        <Card className="relative w-full border border-border/60 bg-card overflow-hidden">
          <CardHeader className="px-5">
            <div className="flex items-center gap-2 min-w-0">
              <h3 className="flex-1 text-lg font-semibold leading-snug tracking-tight">
                {data.title}
              </h3>
              <Badge title={data.difficulty.value} variant={data.difficulty.value as Variant} />
            </div>
          </CardHeader>

          <Separator
            className="mx-5 w-auto"
            style={{ width: "calc(100% - 2.5rem)" }}
          />

          <CardContent className="px-5 overflow-y-auto h-full">
            <div className="flex flex-col gap-6">
              <p className="text-sm text-muted-foreground leading-relaxed">{data.description}</p>

              <div className="flex flex-col gap-4">
                <h4 className="text-base font-semibold tracking-tight">Examples</h4>
                {data.testcases.map((testcase: ITestcase, index: number) => (
                  <Example
                    key={testcase.id}
                    id={index + 1}
                    input={testcase.input.replaceAll(", ", "\n")}
                    expected={testcase.expected.replaceAll(", ", "\n")}
                  />
                ))}
              </div>
            </div>
          </CardContent>
        </Card>

        <div className="flex flex-col w-full gap-2 min-w-0">
          <Card className="relative w-full h-full border border-border/60 bg-card overflow-hidden flex flex-col">
            <CardHeader className="px-5">
              <div className="flex items-center justify-between">
                <h3 className="text-base font-semibold leading-snug tracking-tight">Editor</h3>
                <button
                  onClick={() => submitSolution(data.id)}
                  className={cn(
                    "flex flex-row gap-2 items-center text-chart-2 font-normal text-sm rounded-3xl px-2.5 py-1.5 hover:cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed",
                    isSubmitting && "opacity-50 cursor-not-allowed"
                  )}
                  disabled={!keycloakContext.authenticated || isSubmitting}
                >
                  {isSubmitting ? <Loader2 size={"1rem"} className="animate-spin" /> : <Play size={"1rem"} />}
                </button>
              </div>
            </CardHeader>
            <Separator
              className="mx-5 w-auto"
              style={{ width: "calc(100% - 2.5rem)" }}
            />
            <CardContent className="px-5 flex-1 min-h-0">
              <Editor problemId={data.id} />
            </CardContent>
          </Card>

          <ProblemSubmissions problemId={data.id} />

        </div>
      </div>
    </div>
  );
};
