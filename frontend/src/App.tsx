import { createBrowserRouter, Outlet } from "react-router";
import "./index.css";
import { ProblemsPage } from "./pages/ProblemsPage";
import {
  type IProblem,
  type ProblemData,
  ProblemPage,
} from "./pages/ProblemPage";
import { RouterProvider } from "react-router";
import axios from "axios";
import { keycloakContext } from "./features/KeycloakWrapper";
import type { SubmissionStatus } from "./shared/Submission";

export interface SubmissionsResponse {
  submissions: Page<ISubmission>;
  is_accepted: boolean;
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
      path: "problem/:id",
      loader: async ({ params }): Promise<IProblem> => {
        const problem = await axios.get<IProblem>(
          "http://problem.ucode.com/api/v1/problems/" + params.id,
        );

        return problem.data;
      },
      Component: ProblemPage,
    },
  ]);

  return <RouterProvider router={router} />;
}

export default App;
