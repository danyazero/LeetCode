import { useEffect, useState } from "react";
import axios, { AxiosError } from "axios";
import { keycloakContext } from "@/features/KeycloakWrapper";
import { RequestError, type ErrorInfo } from "@/widget/Error";

export const useQuery = <T,>(url: string) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<ErrorInfo>();
  const [data, setData] = useState<T>();

  useEffect(() => {
    setLoading(true);
    setError(undefined);

    axios
      .get<T>(url, {
        headers: {
          Authorization: `Bearer ${keycloakContext.token}`,
        },
      })
      .then((response) => {
        if (response.status === 200) {
          setData(response.data);
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
    data,
  };
};
