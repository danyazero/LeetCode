import { create } from "zustand";
import axios from "axios";
import { keycloakContext } from "@/features/KeycloakWrapper";
import { Client } from "@stomp/stompjs";

const TERMINAL_STATUSES = [
  "UNSUPPORTED_LANGUAGE",
  "COMPILATION_ERROR",
  "TIME_LIMIT_EXCEEDED",
  "MEMORY_LIMIT_EXCEEDED",
  "WRONG_ANSWER",
  "PARTIALLY_CORRECT",
  "ACCEPTED",
  "INTERNAL_ERROR",
  "CANCELLED",
];

interface ProblemState {
  code: string;
  isSubmitting: boolean;
  submissionCount: number;
  submissionError?: string;
  submissionStatus?: string;
  submissionId?: number;

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
  submissionStatus: undefined,
  submissionId: undefined,

  setCode: (code) => set({ code, submissionError: undefined }),
  clearSubmissionError: () => set({ submissionError: undefined, submissionStatus: undefined, submissionId: undefined }),

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

    set({ isSubmitting: true, submissionError: undefined, submissionStatus: "CONNECTING...", submissionId: undefined });

    return new Promise((resolve) => {
      let timeoutId: Timer;

      const client = new Client({
        brokerURL: "ws://noty.localhost/api/v1/ws",
        connectHeaders: {
          Authorization: `Bearer ${keycloakContext.token}`,
        },
        reconnectDelay: 0, 
        onConnect: async (frame) => {
          console.log("STOMP onConnect frame:", frame);
          set({ submissionStatus: "CONNECTED" });

          client.subscribe("/user/queue/submission-updates", (message) => {
            console.log("STOMP received message:", message.body);
            if (message.body) {
              try {
                const data = JSON.parse(message.body);
                const status = data.status;
                const newSubmissionId = data.submission_id;
                
                if (status) {
                  set((state) => ({ 
                    submissionStatus: status,
                    submissionId: newSubmissionId || state.submissionId
                  }));
                  
                  if (TERMINAL_STATUSES.includes(status.toUpperCase())) {
                    clearTimeout(timeoutId);
                    client.deactivate();
                    set((state) => ({
                      submissionCount: state.submissionCount + 1,
                      isSubmitting: false
                    }));
                    resolve(true);
                  }
                }
              } catch (e) {
                console.error("Failed to parse message:", message.body);
              }
            }
          });

          try {
            const response = await axios.post(
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
            set((state) => ({ 
               submissionStatus: "SUBMISSION SENT",
               submissionId: response.data?.id || response.data?.submission_id || state.submissionId
            }));
          } catch (error) {
            console.error("Submission failed:", error);
            clearTimeout(timeoutId);
            client.deactivate();
            set({
              submissionError: axios.isAxiosError(error) && error.response?.status === 401
                ? "Sign in to submit a solution."
                : "Failed to submit solution. Please try again.",
              isSubmitting: false,
              submissionStatus: undefined,
            });
            resolve(false);
          }
        },
        onChangeState: (state) => {
           console.log("STOMP state changed to: ", state);
        },
        onStompError: (frame) => {
          console.error("Broker reported error: " + frame.headers["message"]);
          clearTimeout(timeoutId);
          client.deactivate();
          set({ submissionError: "Connection error", isSubmitting: false, submissionStatus: undefined });
          resolve(false);
        },
        onWebSocketError: (event) => {
          console.error("WebSocket error", event);
          clearTimeout(timeoutId);
          client.deactivate();
          set({ submissionError: "Connection error", isSubmitting: false, submissionStatus: undefined });
          resolve(false);
        },
        onWebSocketClose: (event) => {
          console.log("WebSocket close:", event);
        }
      });

      // 15 seconds timeout
      timeoutId = setTimeout(() => {
        client.deactivate();
        set({
          submissionError: "Submission timed out.",
          isSubmitting: false,
          submissionStatus: undefined,
        });
        resolve(false);
      }, 15000);

      client.activate();
    });
  },
}));
