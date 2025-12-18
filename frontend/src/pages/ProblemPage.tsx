import type { SubmissionsResponse } from "@/App";
import { ProblemTag } from "@/shared/ProblemTag";
import { Example } from "@/widget/Example";
import { ProblemSubmissions } from "@/widget/ProblemSubmissions";
import { Window } from "@/widget/Window";
import { FaSignal } from "react-icons/fa6";
import { IoCodeSlash, IoDocumentTextOutline } from "react-icons/io5";
import { MdOutlineSignalCellularAlt } from "react-icons/md";
import { useLoaderData } from "react-router";

export interface IProblem {
  id: number;
  title: string;
  description: string;
  difficulty: IDifficulty;
  testcases: ITestcase[];
}

export interface IDifficulty {
  id: number;
  value: string;
}

export interface ITestcase {
  id: number;
  input: string;
  expected: string;
}

export interface ProblemData {
  problem: IProblem;
  submissions: SubmissionsResponse | null;
}

export const ProblemPage = () => {
  const data = useLoaderData<IProblem>();

  if (!data) {
    return <div>Loading...</div>;
  }

  return (
    <div className="flex flex-col px-6 h-screen">
      <div className="flex flex-row py-5 gap-4 items-center">
        <ProblemTag id={data.id} isCompleted={true} />
        <p className="text-base font-medium">{data.title}</p>
      </div>
      <div className="flex flex-row gap-2 w-full h-full relative mb-4 mr-4">
        <Window icon={IoDocumentTextOutline} title="Statement">
          <div className="flex flex-col gap-6">
            <div className="flex flex-col gap-1">
              <h3 className="text-3xl">{data.title}</h3>
              <p className="text-sm text-gray">{data.difficulty.value}</p>
            </div>
            <p>{data.description}</p>

            <div className="flex flex-col gap-6">
              <p className="text-2xl">Examples</p>
              {data.testcases.map((testcase: ITestcase, index: number) => (
                <Example
                  id={index + 1}
                  input={testcase.input.replaceAll(", ", "\n")}
                  expected={testcase.expected.replaceAll(", ", "\n")}
                />
              ))}
            </div>
          </div>
        </Window>

        <div className="flex flex-col w-full gap-2">
          <Window icon={IoCodeSlash} title="Editor" loginRequired>
            <></>
          </Window>

          <Window
            icon={MdOutlineSignalCellularAlt}
            title="Testing"
            loginRequired
          >
            <ProblemSubmissions problemId={data.id} />
          </Window>
        </div>
      </div>
    </div>
  );
};
