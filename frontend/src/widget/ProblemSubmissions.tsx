import type { SubmissionsResponse } from "@/App";
import { keycloakContext } from "@/features/KeycloakWrapper";
import axios, { AxiosError } from "axios";
import { useEffect, useState, type ReactNode } from "react";
import { Window } from "./Window";
import { AiFillSignal } from "react-icons/ai";
import { Spinner } from "@/shared/Spinner";
import { Submission } from "@/shared/Submission";
import { Error, RequestError, type ErrorInfo } from "./Error";

export const ProblemSubmissions = ({ problemId }: { problemId: number }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<ErrorInfo>();
  const [submissions, setSubmissions] = useState<SubmissionsResponse>();

  useEffect(() => {
    setLoading(true);
    axios
      .get<SubmissionsResponse>(
        `http://submission.ucode.com/api/v1/submissions/problems/${problemId}?size=3`,
        {
          headers: {
            Authorization: `Bearer ${keycloakContext.token}`,
          },
        },
      )
      .then((response) => {
        if (response.status == 200) {
          setSubmissions(response.data);
        } else {
          setError(RequestError.UNKNOWN);
        }
      })
      .catch((error: AxiosError) => {
        if (error.status == 401) {
          setError(RequestError.UNAUTHORIZED);
        } else {
          setError(RequestError.UNKNOWN);
        }
      })
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="flex flex-col gap-4">
      <p className="text-base font-medium">Accepted</p>
      <div className="flex flex-col gap-2">
        {loading ? (
          <Spinner />
        ) : submissions ? (
          submissions.submissions.content.map((submission) => (
            <Submission
              key={"problem_submission_" + submission.submission_id}
              id={submission.submission_id}
              status={submission.status}
            />
          ))
        ) : (
          <div className="flex justify-center items-center w-full h-full">
            {error ? <Error error={error} /> : <p>Empty.</p>}
          </div>
        )}
      </div>
    </div>
  );
};
