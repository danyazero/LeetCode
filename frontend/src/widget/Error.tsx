import { keycloakContext } from "@/features/KeycloakWrapper";
import type { ReactNode } from "react";

export type ErrorInfo = {
  title: string;
  caption: string;
  fix: ReactNode | undefined;
};

export const RequestError = {
  UNAUTHORIZED: {
    title: "Sign in to submit",
    caption: "You must sign in to be able to submit solution for this problem.",
    fix: <button onClick={() => keycloakContext.login()}>Sign in</button>,
  },
  UNKNOWN: {
    title: "Unexpected Error",
    caption: "Something went wrong. Please try again later.",
    fix: undefined,
  },
} as const satisfies Record<string, ErrorInfo>;

export interface ErrorProps {
  error: ErrorInfo;
}

export const Error = ({ error }: ErrorProps) => {
  return (
    <div className="flex flex-row justify-center items-center w-full h-full">
      <div className="flex flex-col gap-2">
        <h3 className="text-xl font-normal">{error.title}</h3>
        <p className="text-base font-light text-gray">{error.caption}</p>
        {error.fix ? error.fix : <></>}
      </div>
    </div>
  );
};
