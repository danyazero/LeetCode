import { FaRegCopy } from "react-icons/fa";

export interface ConsoleProps {
  title: string;
  value: string;
}

export const Console = (props: ConsoleProps) => {
  return (
    <div className="flex flex-col gap-4 w-full">
      <div className="flex flex-row justify-between w-full">
        <p className="text-base font-medium">{props.title}</p>

        <div className="w-fit h-fit cursor-pointer">
          <FaRegCopy color="var(--color-gray)" size="1rem" />
        </div>
      </div>

      <div className="p-2.5 rounded-md bg-foreground text-gray whitespace-pre-line">
        {props.value}
      </div>
    </div>
  );
};
