import { ProblemTag } from "./ProblemTag";

export interface ProblemItemProps {
  id: number;
  title: string;
  difficulty: string;
  isCompleted: boolean;
}

export const ProblemItem = (props: ProblemItemProps) => {
  return (
    <div className="flex flex-row px-6 py-2.5 rounded-lg gap-6 items-center cursor-pointer hover:bg-accent">
      <ProblemTag id={props.id} isCompleted={props.isCompleted} />
      <div className="flex flex-col gap-0.5">
        <p className="font-medium">{props.title}</p>
        <p className="text-gray font-light">{props.difficulty}</p>
      </div>
    </div>
  );
};