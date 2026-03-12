import { FaRegCopy } from "react-icons/fa";

export interface ConsoleProps {
  title: string;
  value: string;
}

export const Console = (props: ConsoleProps) => {
  return (
    <div className="flex flex-col gap-2 w-full">
      <div className="flex flex-row items-center justify-between w-full">
        <p className="text-sm font-medium text-foreground">{props.title}</p>

        <button
          className="p-1 rounded-md text-muted-foreground hover:text-foreground hover:bg-muted transition-colors cursor-pointer"
          onClick={() => navigator.clipboard.writeText(props.value)}
          aria-label={`Copy ${props.title}`}
        >
          <FaRegCopy size="0.875rem" />
        </button>
      </div>

      <div className="rounded-lg border border-border/60 bg-muted/50 px-3 py-2.5 font-mono text-sm text-foreground whitespace-pre-line">
        {props.value}
      </div>
    </div>
  );
};
