import { useState, useCallback, useEffect } from "react";
import { useNavigate } from "react-router";
import { ProblemCard } from "@/entities/ProblemCard";
import { ProblemSearchWidget } from "@/widget/ProblemSearchWidget";
import {
  createDifficulty,
  createTag,
  deleteTag,
  fetchAllTags,
  fetchProblems,
  fetchTagsByQuery,
  type PageDto,
  type Problem,
  type ProblemSearchParams,
  type Tag,
} from "@/api/problems";
import { Skeleton } from "@/components/ui/skeleton";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import type { Variant } from "@/shared/Badge";
import { PaginationControls } from "@/shared/PaginationControls";
import { Header } from "@/widget/Header";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import {
  Loader2,
  MoreHorizontal,
  Plus,
} from "lucide-react";
import { keycloakContext } from "@/features/KeycloakWrapper";

export const ProblemsPage = () => {
  const navigate = useNavigate();
  const [problemsPage, setProblemsPage] = useState<PageDto<Problem>>({
    content: [],
    page_number: 0,
    page_size: 10,
    total_pages: 0,
    is_last: true,
    is_first: true,
  });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isTagDialogOpen, setIsTagDialogOpen] = useState(false);
  const [isDifficultyDialogOpen, setIsDifficultyDialogOpen] = useState(false);
  const [tagValue, setTagValue] = useState("");
  const [difficultyValue, setDifficultyValue] = useState("");
  const [tagError, setTagError] = useState<string | null>(null);
  const [difficultyError, setDifficultyError] = useState<string | null>(null);
  const [isCreatingTag, setIsCreatingTag] = useState(false);
  const [isCreatingDifficulty, setIsCreatingDifficulty] = useState(false);

  const [searchParams, setSearchParams] = useState<ProblemSearchParams>({
    page: 0,
    size: 10,
  });

  const canCreateProblems =
    keycloakContext.authenticated &&
    keycloakContext.tokenParsed?.roles?.includes("problem.edit_problems");
  const canCreateTags =
    keycloakContext.authenticated &&
    keycloakContext.tokenParsed?.roles?.includes("problem.edit_tags");
  const canCreateDifficulties =
    keycloakContext.authenticated &&
    keycloakContext.tokenParsed?.roles?.includes("problem.edit_difficulties");
  const canOpenOptions = canCreateTags || canCreateDifficulties;

  const loadProblems = useCallback(async (params: ProblemSearchParams) => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await fetchProblems(params);
      setProblemsPage(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load problems");
    } finally {
      setIsLoading(false);
    }
  }, []);

  const handleSearch = useCallback(
    (params: ProblemSearchParams & { size: number }) => {
      setSearchParams(params);
      loadProblems(params);
    },
    [loadProblems],
  );

  const handlePageChange = useCallback(
    (page: number) => {
      const nextParams = { ...searchParams, page };
      setSearchParams(nextParams);
      loadProblems(nextParams);
    },
    [loadProblems, searchParams],
  );

  const handleCreateTag = async () => {
    const normalizedTag = tagValue.trim();

    if (!normalizedTag) {
      setTagError("Tag name is required.");
      return;
    }

    setIsCreatingTag(true);
    setTagError(null);

    try {
      await createTag({ tag: normalizedTag });
      setTagValue("");
      setIsTagDialogOpen(false);
    } catch (err) {
      setTagError(err instanceof Error ? err.message : "Failed to create tag");
    } finally {
      setIsCreatingTag(false);
    }
  };

  const handleCreateDifficulty = async () => {
    const normalizedDifficulty = difficultyValue.trim();

    if (!normalizedDifficulty) {
      setDifficultyError("Difficulty name is required.");
      return;
    }

    setIsCreatingDifficulty(true);
    setDifficultyError(null);

    try {
      await createDifficulty({ difficulty: normalizedDifficulty });
      setDifficultyValue("");
      setIsDifficultyDialogOpen(false);
    } catch (err) {
      setDifficultyError(
        err instanceof Error ? err.message : "Failed to create difficulty",
      );
    } finally {
      setIsCreatingDifficulty(false);
    }
  };

  useEffect(() => {
    void loadProblems(searchParams);
  }, [loadProblems]);

  return (
    <div className="flex flex-col w-full h-screen">
      <Header>
        <button onClick={() => console.log(keycloakContext.token)}>
          Token
        </button>
      </Header>
      <div className="flex flex-row justify-center w-full overflow-y-auto">
        <div className="flex flex-col max-w-4xl w-full gap-2 mt-8 px-6 pb-8">
          <div className="flex justify-between items-end mb-2 gap-3">
            <p className="text-4xl font-bold text-foreground">Problems</p>
            <div className="flex items-center gap-2">
              {canCreateProblems && (
                <Button onClick={() => navigate("/create-problem")}>
                  <Plus className="mr-2 h-4 w-4" /> Create Problem
                </Button>
              )}
              {canOpenOptions && (
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button variant="outline">
                      <MoreHorizontal className="h-4 w-4" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end">
                    {canCreateTags && (
                      <>
                        <DropdownMenuItem
                          onClick={() => {
                            setTagError(null);
                            setIsTagDialogOpen(true);
                          }}
                        >
                          Create tag
                        </DropdownMenuItem>
                      </>
                    )}
                    {canCreateDifficulties && (
                      <DropdownMenuItem
                        onClick={() => {
                          setDifficultyError(null);
                          setIsDifficultyDialogOpen(true);
                        }}
                      >
                        Create difficulty
                      </DropdownMenuItem>
                    )}
                  </DropdownMenuContent>
                </DropdownMenu>
              )}
            </div>
          </div>
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
                <Card
                  key={i}
                  className="relative w-full border border-border/60 bg-card"
                >
                  <CardHeader className="px-5">
                    <div className="flex items-start gap-3">
                      <Skeleton className="h-5 w-5 rounded-full" />
                      <div className="flex flex-1 items-center gap-2 min-w-0">
                        <Skeleton className="h-5 flex-1 max-w-[250px]" />
                        <Skeleton className="h-5 w-16" />
                      </div>
                    </div>
                  </CardHeader>
                  <Separator
                    className="mx-5 w-auto"
                    style={{ width: "calc(100% - 2.5rem)" }}
                  />
                  <CardContent className="px-5">
                    <div className="flex items-center justify-between gap-4 flex-wrap">
                      <Skeleton className="h-5 w-24" />
                      <Skeleton className="h-5 w-24" />
                    </div>
                  </CardContent>
                </Card>
              ))
            ) : !error && problemsPage.content.length === 0 ? (
              <p className="text-center text-muted-foreground mt-8">
                No problems found
              </p>
            ) : (
              !error &&
              problemsPage.content.map((problem) => (
                <ProblemCard
                  key={problem.id}
                  isCompleted={false}
                  id={problem.id}
                  title={problem.title}
                  difficulty={problem.difficulty.value as Variant}
                  acceptance_rate={problem.acceptance_rate}
                  submissions={problem.submissions}
                  onClick={() => navigate(`/problem/${problem.id}`)}
                />
              ))
            )}

            {!isLoading && !error ? (
              <PaginationControls
                currentPage={problemsPage.page_number}
                totalPages={problemsPage.total_pages}
                onPageChange={handlePageChange}
              />
            ) : null}
          </div>
        </div>
      </div>

      <Dialog open={isTagDialogOpen} onOpenChange={setIsTagDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Create tag</DialogTitle>
            <DialogDescription>Add a new problem tag.</DialogDescription>
          </DialogHeader>
          <div className="flex flex-col gap-2">
            <Input
              value={tagValue}
              onChange={(event) => {
                setTagValue(event.target.value);
                setTagError(null);
              }}
              placeholder="Tag name"
              disabled={isCreatingTag}
            />
            {tagError ? (
              <p className="text-sm text-destructive">{tagError}</p>
            ) : null}
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setIsTagDialogOpen(false)}
              disabled={isCreatingTag}
            >
              Cancel
            </Button>
            <Button onClick={handleCreateTag} disabled={isCreatingTag}>
              {isCreatingTag ? "Creating..." : "Create"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog
        open={isDifficultyDialogOpen}
        onOpenChange={setIsDifficultyDialogOpen}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Create difficulty</DialogTitle>
            <DialogDescription>Add a new problem difficulty.</DialogDescription>
          </DialogHeader>
          <div className="flex flex-col gap-2">
            <Input
              value={difficultyValue}
              onChange={(event) => {
                setDifficultyValue(event.target.value);
                setDifficultyError(null);
              }}
              placeholder="Difficulty name"
              disabled={isCreatingDifficulty}
            />
            {difficultyError ? (
              <p className="text-sm text-destructive">{difficultyError}</p>
            ) : null}
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setIsDifficultyDialogOpen(false)}
              disabled={isCreatingDifficulty}
            >
              Cancel
            </Button>
            <Button
              onClick={handleCreateDifficulty}
              disabled={isCreatingDifficulty}
            >
              {isCreatingDifficulty ? "Creating..." : "Create"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};
