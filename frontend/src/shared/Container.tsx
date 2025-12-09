import type { ReactNode } from "react";

export interface ContainerProps {
  children: ReactNode | ReactNode[];
}

export const Container = (props: ContainerProps) => {
  return <div className="bg-foreground rounded-xl py-2">{props.children}</div>;
};
