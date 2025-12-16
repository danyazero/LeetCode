import { Console } from "@/shared/Console";

export interface ExampleProps {
  id: number;
  input: string;
  expected: string;
}

export const Example = (props: ExampleProps) => {
  return (
    <div className="flex flex-row w-full gap-4">
      <Console title={`Input #${props.id}`} value={props.input} />
      <Console title={`Answer #${props.id}`} value={props.expected} />
    </div>
  );
};
