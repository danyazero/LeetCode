export interface TagProps {
  id: number;
  isCompleted: boolean;
}

export const ProblemTag = (props: TagProps) => {
  return (
    <div
      className={
        "flex justify-center items-center py-1 rounded-lg w-16 " +
        (props.isCompleted ? "bg-green-500 text-white" : "bg-secondary")
      }
    >
      {props.id}
    </div>
  );
};
