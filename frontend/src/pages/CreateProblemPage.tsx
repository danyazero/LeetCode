import * as React from "react";
import { useNavigate } from "react-router";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from "@/components/ui/card";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Plus, Trash2, ChevronLeft, ChevronRight, X } from "lucide-react";
import { fetchDifficulties, fetchAllTags, createProblem, createTestcase } from "@/api/problems";
import type { Difficulty, Tag, CreateProblemDto, TestcaseDto } from "@/api/problems";
import { cn } from "@/lib/utils";

interface Testcase {
  input: string;
  expected: string;
  is_public: boolean;
}

export function CreateProblemPage() {
  const navigate = useNavigate();
  const [step, setStep] = React.useState(1);
  const [isLoading, setIsLoading] = React.useState(false);

  // Step 1 State
  const [title, setTitle] = React.useState("");
  const [description, setDescription] = React.useState("");
  const [selectedDifficulty, setSelectedDifficulty] = React.useState<Difficulty | null>(null);
  const [selectedTags, setSelectedTags] = React.useState<Tag[]>([]);

  // Data for selections
  const [difficulties, setDifficulties] = React.useState<Difficulty[]>([]);
  const [availableTags, setAvailableTags] = React.useState<Tag[]>([]);
  const [tagSearch, setTagSearch] = React.useState("");

  // Step 2 State
  const [testcases, setTestcases] = React.useState<Testcase[]>([{ input: "", expected: "", is_public: true }]);

  React.useEffect(() => {
    fetchDifficulties().then((page) => setDifficulties(page.content));
    fetchAllTags(0, 50).then((page) => setAvailableTags(page.content));
  }, []);

  const handleAddTestcase = () => {
    setTestcases([...testcases, { input: "", expected: "", is_public: true }]);
  };

  const handleRemoveTestcase = (index: number) => {
    setTestcases(testcases.filter((_, i) => i !== index));
  };

  const handleTestcaseChange = (index: number, field: keyof Testcase, value: string | boolean) => {
    setTestcases((prev) => {
      const next = [...prev];
      const tc = next[index];
      if (tc) {
        next[index] = { ...tc, [field]: value };
      }
      return next;
    });
  };

  const handleTagSelect = (tag: Tag) => {
    if (!selectedTags.find((t) => t.id === tag.id)) {
      setSelectedTags([...selectedTags, tag]);
    }
    setTagSearch("");
  };

  const handleRemoveTag = (tagId: number) => {
    setSelectedTags(selectedTags.filter((t) => t.id !== tagId));
  };

  const handleSubmit = async () => {
    if (!selectedDifficulty) return;

    setIsLoading(true);
    try {
      const problemData: CreateProblemDto = {
        title,
        description,
        difficulty: selectedDifficulty,
        tags: selectedTags,
      };

      const createdProblem = await createProblem(problemData);

      // Create testcases
      await Promise.all(
        testcases.map((tc) =>
          createTestcase({
            input: tc.input,
            expected: tc.expected,
            is_public: tc.is_public,
            problem_id: createdProblem.id,
          })
        )
      );

      navigate(`/problem/${createdProblem.id}`);
    } catch (error) {
      console.error("Failed to create problem:", error);
      alert("Failed to create problem. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  const filteredTags = availableTags.filter(
    (tag) =>
      tag.value.toLowerCase().includes(tagSearch.toLowerCase()) &&
      !selectedTags.some((st) => st.id === tag.id)
  ).slice(0, 10);

  return (
    <div className="container mx-auto py-10 max-w-4xl">
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Create New Problem</h1>
          <p className="text-muted-foreground">Define a new challenge for the community.</p>
        </div>
        <div className="flex items-center gap-2">
          <div className={cn("w-8 h-8 rounded-full flex items-center justify-center font-bold text-sm", step === 1 ? "bg-primary text-primary-foreground" : "bg-muted text-muted-foreground")}>1</div>
          <div className="w-10 h-[2px] bg-muted" />
          <div className={cn("w-8 h-8 rounded-full flex items-center justify-center font-bold text-sm", step === 2 ? "bg-primary text-primary-foreground" : "bg-muted text-muted-foreground")}>2</div>
        </div>
      </div>

      <Card className="shadow-lg border-2">
        <CardHeader>
          <CardTitle>
            {step === 1 ? "Problem Details" : "Test Cases"}
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-6">
          {step === 1 ? (
            <>
              <div className="space-y-2">
                <Label htmlFor="title">Title</Label>
                <Input
                  id="title"
                  placeholder="e.g. Two Sum"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  className="text-lg"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="difficulty">Difficulty</Label>
                  <Select
                    onValueChange={(val) => {
                      const d = difficulties.find(d => d.id === parseInt(val));
                      if (d) setSelectedDifficulty(d);
                    }}
                  >
                    <SelectTrigger id="difficulty">
                      <SelectValue placeholder="Select difficulty" />
                    </SelectTrigger>
                    <SelectContent>
                      {difficulties.map((d) => (
                        <SelectItem key={d.id} value={d.id.toString()}>
                          {d.value}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label>Tags</Label>
                  <div className="relative">
                    <Input
                      placeholder="Search tags..."
                      value={tagSearch}
                      onChange={(e) => setTagSearch(e.target.value)}
                    />
                    {tagSearch && filteredTags.length > 0 && (
                      <div className="absolute z-10 w-full mt-1 bg-popover border rounded-md shadow-md max-h-40 overflow-y-auto">
                        {filteredTags.map((tag) => (
                          <button
                            key={tag.id}
                            className="w-full px-3 py-2 text-left hover:bg-accent hover:text-accent-foreground text-sm"
                            onClick={() => handleTagSelect(tag)}
                          >
                            {tag.value}
                          </button>
                        ))}
                      </div>
                    )}
                  </div>
                  <div className="flex flex-wrap gap-2 mt-2">
                    {selectedTags.map((tag) => (
                      <Badge key={tag.id} variant="secondary" className="pl-2 pr-1 gap-1">
                        {tag.value}
                        <button onClick={() => handleRemoveTag(tag.id)}>
                          <X size={14} className="hover:text-destructive" />
                        </button>
                      </Badge>
                    ))}
                  </div>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="description">Description (Markdown supported)</Label>
                <Textarea
                  id="description"
                  placeholder="Problem description..."
                  className="min-h-[200px] font-mono"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                />
              </div>
            </>
          ) : (
            <div className="space-y-6">
              {testcases.map((tc, index) => (
                <div key={index} className="p-4 border rounded-lg bg-muted/30 relative">
                  <div className="flex items-center justify-between mb-4">
                    <div className="flex items-center gap-4">
                      <h3 className="font-semibold text-sm">Test Case #{index + 1}</h3>
                      <Button
                        variant={tc.is_public ? "default" : "outline"}
                        size="sm"
                        className="h-7 text-[10px] px-2"
                        onClick={() => handleTestcaseChange(index, "is_public", !tc.is_public)}
                      >
                        {tc.is_public ? "Public" : "Private"}
                      </Button>
                    </div>
                    {testcases.length > 1 && (
                      <Button
                        variant="ghost"
                        size="icon"
                        className="text-destructive hover:text-destructive hover:bg-destructive/10"
                        onClick={() => handleRemoveTestcase(index)}
                      >
                        <Trash2 size={16} />
                      </Button>
                    )}
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label>Input</Label>
                      <Textarea
                        placeholder="e.g. [2,7,11,15], 9"
                        className="font-mono text-sm"
                        value={tc.input}
                        onChange={(e) => handleTestcaseChange(index, "input", e.target.value)}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label>Expected Output</Label>
                      <Textarea
                        placeholder="e.g. [0,1]"
                        className="font-mono text-sm"
                        value={tc.expected}
                        onChange={(e) => handleTestcaseChange(index, "expected", e.target.value)}
                      />
                    </div>
                  </div>
                </div>
              ))}
              <Button
                variant="outline"
                className="w-full border-dashed py-6"
                onClick={handleAddTestcase}
              >
                <Plus size={16} className="mr-2" /> Add Test Case
              </Button>
            </div>
          )}
        </CardContent>
        <CardFooter className="flex justify-between border-t p-6 bg-muted/10">
          {step === 1 ? (
            <Button variant="ghost" onClick={() => navigate("/")}>
              Cancel
            </Button>
          ) : (
            <Button variant="outline" onClick={() => setStep(1)}>
              <ChevronLeft size={16} className="mr-2" /> Previous Step
            </Button>
          )}

          {step === 1 ? (
            <Button
              onClick={() => setStep(2)}
              disabled={!title || !description || !selectedDifficulty}
            >
              Continue to Test Cases <ChevronRight size={16} className="ml-2" />
            </Button>
          ) : (
            <Button
              onClick={handleSubmit}
              disabled={isLoading || testcases.some(tc => !tc.input || !tc.expected)}
            >
              {isLoading ? "Creating..." : "Create Problem"}
            </Button>
          )}
        </CardFooter>
      </Card>
    </div>
  );
}
