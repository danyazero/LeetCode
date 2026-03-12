import type { SubmissionsResponse } from "@/App";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Badge, type Variant } from "@/shared/Badge";
import { ProblemTag } from "@/shared/ProblemTag";
import { Example } from "@/widget/Example";
import { ProblemSubmissions } from "@/widget/ProblemSubmissions";
import { useLoaderData } from "react-router";
import { Editor, type EditorHandle } from "@/widget/Editor";
import { keycloakContext } from "@/features/KeycloakWrapper";
import { useRef } from "react";
import { Play } from "lucide-react";

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
  const editorRef = useRef<EditorHandle>(null);

  if (!data) {
    return <div>Loading...</div>;
  }

  return (
    <div className="flex flex-col px-6 h-screen">
      <div className="flex flex-row py-5 gap-4 items-center">
        <ProblemTag id={data.id} isCompleted={true} />
        <p onClick={() => keycloakContext.logout()} className="text-base font-medium">{data.title}</p>
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
                  onClick={() => editorRef.current?.submit()}
                  className="flex flex-row gap-2 items-center text-chart-2 font-normal text-sm rounded-3xl px-2.5 py-1.5 hover:cursor-pointer"
                >
                  <Play size={"1rem"} />
                </button>
              </div>
            </CardHeader>
            <Separator
              className="mx-5 w-auto"
              style={{ width: "calc(100% - 2.5rem)" }}
            />
            <CardContent className="px-5 flex-1 min-h-0">
              <Editor ref={editorRef} problemId={data.id} />
            </CardContent>
          </Card>

          <ProblemSubmissions problemId={data.id} />

        </div>
      </div>
    </div>
  );
};
