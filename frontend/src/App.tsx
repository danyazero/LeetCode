import { createBrowserRouter, RouterProvider } from "react-router";
import "./index.css";
import { ProblemsPage } from "./pages/ProblemsPage";

export function App() {
  
  const router = createBrowserRouter([
    {
      path: "/",
      Component: ProblemsPage,
    }
  ]);

  return <RouterProvider router={router} />;
}

export default App;
