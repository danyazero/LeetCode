import { create } from "zustand";
import axios from "axios";
import { keycloakContext } from "@/features/KeycloakWrapper";

interface ProblemState {
  code: string;
  isSubmitting: boolean;
  submissionCount: number;
  submissionError?: string;

  // Actions
  setCode: (code: string) => void;
  clearSubmissionError: () => void;
  submitSolution: (problemId: number, languageId?: number) => Promise<boolean>;
}

export const useProblemStore = create<ProblemState>((set, get) => ({
  code: "",
  isSubmitting: false,
  submissionCount: 0,
  submissionError: undefined,

  setCode: (code) => set({ code, submissionError: undefined }),
  clearSubmissionError: () => set({ submissionError: undefined }),

  submitSolution: async (problemId, languageId) => {
    const { code } = get();
    if (get().isSubmitting) return false;
    if (!code.trim()) {
      set({ submissionError: "Write a solution before submitting." });
      return false;
    }
    if (!languageId) {
      set({ submissionError: "Select a compiler before submitting." });
      return false;
    }

    set({ isSubmitting: true, submissionError: undefined });
    try {
      await axios.post(
        `http://submission.localhost/api/v1/submissions`,
        {
          problem_id: problemId,
          solution: code,
          language_id: languageId,
        },
        {
          headers: {
            Authorization: `Bearer ${keycloakContext.token}`,
          },
        },
      );
      set((state) => ({ submissionCount: state.submissionCount + 1 }));
      return true;
    } catch (error) {
      console.error("Submission failed:", error);
      set({
        submissionError: axios.isAxiosError(error) && error.response?.status === 401
          ? "Sign in to submit a solution."
          : "Failed to submit solution. Please try again.",
      });
      return false;
    } finally {
      set({ isSubmitting: false });
    }
  },
}));
