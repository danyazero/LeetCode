import { keycloakContext } from "@/features/KeycloakWrapper";
import { Tab } from "@/shared/Tab";
import type { ReactNode } from "react";
import type { IconType } from "react-icons";
import { Error, RequestError } from "./Error";

export interface WindowProps {
  icon: IconType;
  title: string;
  loginRequired?: boolean;
  children: ReactNode | ReactNode[];
}

export const Window = (props: WindowProps) => {
  if (!keycloakContext.token && props.loginRequired) {
    return (
      <Window icon={props.icon} title={props.title}>
        <Error error={RequestError.UNAUTHORIZED} />
      </Window>
    );
  }

  return (
    <div className="flex flex-col rounded-lg bg-white shadow-md w-full h-full">
      <div className="flex flex-row w-full bg-foreground rounded-t-lg">
        <Tab icon={props.icon} text={props.title} isActive />
      </div>
      <>{props.children}</>
    </div>
  );
};
