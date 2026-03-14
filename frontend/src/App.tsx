import { createBrowserRouter } from "react-router";
import "./index.css";
import { ProblemsPage } from "./pages/ProblemsPage";
import { type IProblem, ProblemPage } from "./pages/ProblemPage";
import { CreateProblemPage } from "./pages/CreateProblemPage";
import { RouterProvider } from "react-router";
import axios from "axios";
import type { SubmissionStatus } from "./shared/Submission";

// This will be used for the new submissions list endpoint
export type SubmissionsPage = Page<ISubmission>;

export interface ProblemStatus {
  is_solved: boolean;
}

export interface Page<T> {
  content: T[];
  page_number: number;
  page_size: number;
  total_pages: number;
  is_last: boolean;
  is_first: boolean;
}

export interface ISubmission {
  submission_id: number;
  language: string;
  status: SubmissionStatus;
  created_at: string;
}

export function App() {
  const router = createBrowserRouter([
    {
      path: "/",
      Component: ProblemsPage,
    },
    {
      path: "/create-problem",
      Component: CreateProblemPage,
    },
    {
      path: "problem/:id",
      loader: async ({ params }): Promise<IProblem> => {
        const problem = await axios.get<IProblem>(
          "http://problem.localhost/api/v1/problems/" + params.id,
        );

        return problem.data;
      },
      Component: ProblemPage,
    },
  ]);

  return <RouterProvider router={router} />;
}

export default App;
