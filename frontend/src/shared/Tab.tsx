import type { IconType } from "react-icons";

export interface TabProps {
  icon: IconType;
  text: string;
  isActive: boolean;
}

export const Tab = (props: TabProps) => {
  return (
    <div className={"flex flex-row gap-2 text-sm items-center px-3 py-2 border-b-2 " + (props.isActive ? "border-green" : "border-transparent")}>
      <props.icon size={20} color="var(--color-green)" />
      <p className="text-sm text-green font-normal">{props.text}</p>
    </div>
  );
};
