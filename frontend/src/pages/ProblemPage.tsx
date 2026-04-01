import type { SubmissionsPage } from "@/App";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { ProblemTags } from "@/widget/ProblemTags";
import { Badge, type Variant } from "@/shared/Badge";
import { Example } from "@/widget/Example";
import { ProblemSubmissions } from "@/widget/ProblemSubmissions";
import { useNavigate, useLoaderData } from "react-router";
import { Editor } from "@/widget/Editor";
import { useEffect, useState } from "react";
import { useProblemStore } from "@/features/Problem/store/useProblemStore";
import { cn } from "@/lib/utils";
import { Play, Loader2, Trash2, MoreHorizontal } from "lucide-react";
import { keycloakContext } from "@/features/KeycloakWrapper";
import { Header } from "@/widget/Header";
import { Button } from "@/components/ui/button";
import { deleteProblem, type Tag } from "@/api/problems";
import {
  fetchAllLanguages,
  type Language,
  type SubmissionDetails,
} from "@/api/submissions";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";

export interface IProblem {
  id: number;
  title: string;
  description: string;
  difficulty: IDifficulty;
  tags: Tag[];
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
  submissions: SubmissionsPage | null;
}

export const ProblemPage = () => {
  const data = useLoaderData<IProblem>();
  const navigate = useNavigate();
  const submitSolution = useProblemStore((state) => state.submitSolution);
  const isSubmitting = useProblemStore((state) => state.isSubmitting);
  const setCode = useProblemStore((state) => state.setCode);
  const clearSubmissionError = useProblemStore(
    (state) => state.clearSubmissionError,
  );
  const [isDeleting, setIsDeleting] = useState(false);
  const [languages, setLanguages] = useState<Language[]>([]);
  const [languagesLoading, setLanguagesLoading] = useState(true);
  const [selectedLanguageId, setSelectedLanguageId] = useState("");

  const handleDelete = async () => {
    setIsDeleting(true);
    try {
      await deleteProblem(data.id);
      navigate("/");
    } catch (error) {
      console.error("Failed to delete problem:", error);
      alert("Failed to delete problem.");
    } finally {
      setIsDeleting(false);
    }
  };

  const handleRestoreSubmission = (submission: SubmissionDetails) => {
    setCode(submission.solution ?? "");
    clearSubmissionError();

    if (submission.language?.id != null) {
      setSelectedLanguageId(submission.language.id.toString());
    }
  };

  useEffect(() => {
    setCode("");
    clearSubmissionError();
    setSelectedLanguageId("");
  }, [clearSubmissionError, setCode, data?.id]);

  useEffect(() => {
    let isMounted = true;

    setLanguagesLoading(true);

    fetchAllLanguages()
      .then((response) => {
        if (!isMounted) return;
        setLanguages(response.languages);
        if (response.last_used != null) {
          setSelectedLanguageId(response.last_used.toString());
        }
      })
      .catch((error) => {
        console.error("Failed to load compilers:", error);
        if (!isMounted) return;
        setLanguages([]);
      })
      .finally(() => {
        if (isMounted) {
          setLanguagesLoading(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, []);

  if (!data) {
    return <div>Loading...</div>;
  }

  const isSubmitDisabled =
    !keycloakContext.authenticated ||
    isSubmitting ||
    languagesLoading ||
    !selectedLanguageId;

  return (
    <div className="flex flex-col h-screen overflow-hidden gap-2">
      <Header />
      <div className="flex flex-row gap-2 w-full h-full relative mb-4 mr-4">
        <Card className="relative w-full border border-border/60 bg-card overflow-hidden">
          <CardHeader className="px-5">
            <div className="flex items-center gap-2 min-w-0">
              <h3 className="flex-1 text-lg font-semibold leading-snug tracking-tight">
                {data.title}
              </h3>
              {keycloakContext.authenticated &&
                keycloakContext.tokenParsed?.roles?.includes(
                  "problem.edit_problems",
                ) && (
                  <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                      <Button
                        variant="ghost"
                        size="icon"
                        className="h-8 w-8"
                        disabled={isDeleting}
                      >
                        {isDeleting ? (
                          <Loader2 className="h-4 w-4 animate-spin" />
                        ) : (
                          <MoreHorizontal className="h-4 w-4" />
                        )}
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      <DropdownMenuItem
                        variant="destructive"
                        onClick={handleDelete}
                        disabled={isDeleting}
                      >
                        <Trash2 className="mr-2 h-4 w-4" />
                        <span>Delete problem</span>
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                )}
              <Badge
                title={data.difficulty.value}
                variant={data.difficulty.value as Variant}
              />
            </div>
          </CardHeader>

          <Separator
            className="mx-5 w-auto"
            style={{ width: "calc(100% - 2.5rem)" }}
          />

          <CardContent className="px-5 overflow-y-auto h-full">
            <div className="flex flex-col gap-6">
              <ProblemTags tags={data.tags} />
              <p className="text-sm text-muted-foreground leading-relaxed">
                {data.description}
              </p>

              <div className="flex flex-col gap-4">
                <h4 className="text-base font-semibold tracking-tight">
                  Examples
                </h4>
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
              <div className="flex items-start justify-between gap-3">
                <h3 className="text-base font-semibold leading-snug tracking-tight">
                  Editor
                </h3>
                <div className="flex flex-col items-end gap-2">
                  <div className="flex items-center gap-2">
                    {languagesLoading ? (
                      <Skeleton className="h-8 w-40 rounded-lg" />
                    ) : (
                      <Select
                        value={selectedLanguageId}
                        onValueChange={(value) => {
                          setSelectedLanguageId(value);
                          clearSubmissionError();
                        }}
                        disabled={isSubmitting || !keycloakContext.authenticated}
                      >
                        <SelectTrigger className="w-40">
                          <SelectValue placeholder="Select compiler" />
                        </SelectTrigger>
                        <SelectContent>
                          {languages.map((language) => (
                            <SelectItem
                              key={language.id}
                              value={String(language.id)}
                            >
                              {language.language}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    )}
                    <button
                      onClick={() =>
                        submitSolution(data.id, Number(selectedLanguageId))
                      }
                      className={cn(
                        "flex flex-row gap-2 items-center text-chart-2 font-normal text-sm rounded-3xl px-2.5 py-1.5 hover:cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed",
                        isSubmitting && "opacity-50 cursor-not-allowed",
                      )}
                      disabled={isSubmitDisabled}
                    >
                      {isSubmitting ? (
                        <Loader2 size={"1rem"} className="animate-spin" />
                      ) : (
                        <Play size={"1rem"} />
                      )}
                    </button>
                  </div>
                </div>
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

          <ProblemSubmissions
            problemId={data.id}
            onRestoreSubmission={handleRestoreSubmission}
          />
        </div>
      </div>
    </div>
  );
};
