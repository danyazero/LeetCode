import { Tab } from "@/shared/Tab";
import type { ReactNode } from "react";
import type { IconType } from "react-icons";

export interface WindowProps {
  icon: IconType;
  title: string;
  children: ReactNode | ReactNode[];
}

export const Window = (props: WindowProps) => {
  return (
    <div className="flex flex-col rounded-lg bg-white shadow-md w-full min-h-[80vh]">
      <div className="flex flex-row w-full bg-foreground rounded-t-lg">
        <Tab icon={props.icon} text={props.title} isActive/>
      </div>
      <div className="px-6 py-8">{props.children}</div>
    </div>
  );
};