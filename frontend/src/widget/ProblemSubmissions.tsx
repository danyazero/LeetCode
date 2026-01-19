import type { SubmissionsResponse } from "@/App";
import { keycloakContext } from "@/features/KeycloakWrapper";
import axios, { AxiosError } from "axios";
import { useEffect, useState } from "react";
import { Spinner } from "@/shared/Spinner";
import { Submission } from "@/shared/Submission";
import { Error, RequestError, type ErrorInfo } from "./Error";
import { useQuery } from "@/features/QueryHook";

export const ProblemSubmissions = ({ problemId }: { problemId: number }) => {
  const { loading, submissions, error } = useQuery(
    `http://submission.ucode.com/api/v1/submissions/problems/${problemId}?size=${10}`,
  );

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
