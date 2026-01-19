import { useEffect, useState } from "react";
import axios, { AxiosError } from "axios";
import type { SubmissionsResponse } from "@/App";
import { keycloakContext } from "@/features/KeycloakWrapper";
import { RequestError, type ErrorInfo } from "@/widget/Error";

export const useQuery = (url: string) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<ErrorInfo>();
  const [submissions, setSubmissions] = useState<SubmissionsResponse>();

  useEffect(() => {
    setLoading(true);
    setError(undefined);

    axios
      .get<SubmissionsResponse>(url, {
        headers: {
          Authorization: `Bearer ${keycloakContext.token}`,
        },
      })
      .then((response) => {
        if (response.status === 200) {
          setSubmissions(response.data);
        } else {
          setError(RequestError.UNKNOWN);
        }
      })
      .catch((error: AxiosError) => {
        if (error.response?.status === 401) {
          setError(RequestError.UNAUTHORIZED);
        } else {
          setError(RequestError.UNKNOWN);
        }
      })
      .finally(() => setLoading(false));
  }, [url]);

  return {
    loading,
    error,
    submissions,
  };
};
