import { useState } from "react";
import { keycloakContext } from "@/features/KeycloakWrapper";
import { ProblemCard, type ProblemCardProps } from "@/entities/ProblemCard";
import { ProblemSearchWidget } from "@/widget/ProblemSearchWidget";

export const ProblemsPage = () => {
  const [search, setSearch] = useState("");

  const SAMPLE_TASKS: ProblemCardProps[] = [
    {
      id: 1,
      title: "Two Sum",
      difficulty: "Easy",
      acceptanceRate: 52.3,
      submissionsCount: 14_800_000,
      isCompleted: true,
    },
    {
      id: 3,
      title: "Longest Substring Without Repeating Characters",
      difficulty: "Medium",
      acceptanceRate: 34.1,
      submissionsCount: 9_200_000,
      isCompleted: true,
    },
    {
      id: 4,
      title: "Median of Two Sorted Arrays",
      difficulty: "Hard",
      acceptanceRate: 38.7,
      submissionsCount: 5_100_000,
      isCompleted: false,
    },
    {
      id: 23,
      title: "Merge K Sorted Lists",
      difficulty: "Hard",
      acceptanceRate: 51.8,
      submissionsCount: 3_700_000,
      isCompleted: false,
    },
    {
      id: 53,
      title: "Maximum Subarray",
      difficulty: "Medium",
      acceptanceRate: 50.5,
      submissionsCount: 7_400_000,
      isCompleted: true,
    },
    {
      id: 121,
      title: "Best Time to Buy and Sell Stock",
      difficulty: "Easy",
      acceptanceRate: 58.2,
      submissionsCount: 8_900_000,
      isCompleted: false,
    },
    {
      id: 200,
      title: "Number of Islands",
      difficulty: "Medium",
      acceptanceRate: 57.4,
      submissionsCount: 6_100_000,
      isCompleted: false,
    },
    {
      id: 295,
      title: "Find Median from Data Stream",
      difficulty: "Hard",
      acceptanceRate: 51.3,
      submissionsCount: 1_900_000,
      isCompleted: true,
    },
  ];

  const MOCK_DIFFICULTIES = [
    { id: 1, name: "Easy" },
    { id: 2, name: "Medium" },
    { id: 3, name: "Hard" },
  ];

  const MOCK_TAGS = [
    { id: 1, value: "Array" },
    { id: 2, value: "Hash Table" },
    { id: 3, value: "Linked List" },
    { id: 4, value: "Dynamic Programming" },
    { id: 5, value: "Graph" },
    { id: 6, value: "BFS" },
    { id: 7, value: "DFS" },
    { id: 8, value: "Sliding Window" },
    { id: 9, value: "Two Pointers" },
    { id: 10, value: "Binary Search" },
    { id: 11, value: "Backtracking" },
    { id: 12, value: "Greedy" },
    { id: 13, value: "Heap / Priority Queue" },
    { id: 14, value: "Stack" },
    { id: 15, value: "Trie" },
  ];

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
        <ProblemSearchWidget onSearch={(a) => console.log(a)} />

        {SAMPLE_TASKS.map((task) => (
          <ProblemCard
            key={task.id}
            {...task}
            onClick={() => console.log(task.id)}
          />
        ))}
      </div>
    </div>
  );
};
