import { create } from "zustand";
import axios from "axios";
import { keycloakContext } from "@/features/KeycloakWrapper";

interface ProblemState {
  code: string;
  isSubmitting: boolean;
  submissionCount: number;
  
  // Actions
  setCode: (code: string) => void;
  submitSolution: (problemId: number) => Promise<void>;
}

export const useProblemStore = create<ProblemState>((set, get) => ({
  code: "",
  isSubmitting: false,
  submissionCount: 0,

  setCode: (code) => set({ code }),

  submitSolution: async (problemId) => {
    const { code } = get();
    if (!code || get().isSubmitting) return;

    set({ isSubmitting: true });
    try {
      await axios.post(
        `http://submission.localhost/api/v1/submissions`,
        {
          problem_id: problemId,
          solution: code,
          language_id: 1,
        },
        {
          headers: {
            Authorization: `Bearer ${keycloakContext.token}`,
          },
        },
      );
      set((state) => ({ submissionCount: state.submissionCount + 1 }));
    } catch (error) {
      console.error("Submission failed:", error);
    } finally {
      set({ isSubmitting: false });
    }
  },
}));
