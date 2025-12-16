import type { IconType } from "react-icons";

export interface TagProps {
  icon: IconType;
  text: string;
}

export const Tag = (props: TagProps) => {
  return (
    <div className="flex flex-row rounded-lg px-2 py-1.5 bg-accent w-fit items-center">
      <props.icon color="var(--color-black)" size={18} />
      <p className="text-sm px-2 text-black">{props.text}</p>
    </div>
  );
};