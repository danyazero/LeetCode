import { createBrowserRouter, Outlet } from "react-router";
import "./index.css";
import { ProblemsPage } from "./pages/ProblemsPage";
import { type IProblem, ProblemPage } from "./pages/ProblemPage";
import { RouterProvider } from "react-router";
import axios from "axios";

export function App() {
  const router = createBrowserRouter([
    {
      path: "/",
      Component: ProblemsPage,
    },
    {
      path: "problem/:id",
      loader: async ({ params }) => {
        return await axios.get<IProblem>(
          "http://problem.ucode.com/api/v1/problems/" + params.id,
        );
      },
      Component: ProblemPage,
    },
  ]);

  return <RouterProvider router={router} />;
}

export default App;
