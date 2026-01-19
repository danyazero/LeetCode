// import { basicSetup } from "codemirror";
import { EditorState } from "@codemirror/state";
import { LiaArrowCircleUpSolid } from "react-icons/lia";
import {
  EditorView,
  highlightActiveLine,
  highlightActiveLineGutter,
  lineNumbers,
} from "@codemirror/view";
import { useEffect, useRef } from "react";
import axios from "axios";
import { keycloakContext } from "@/features/KeycloakWrapper";

export interface EditorProps {
  problemId: number;
}

export const Editor = (props: EditorProps) => {
  const editorRef = useRef<HTMLDivElement>(null);
  const viewRef = useRef<EditorView | null>(null);

  useEffect(() => {
    if (!editorRef.current) return;
    const noOutline = EditorView.theme({
      "&": { outline: "none" },
      "&.cm-focused": { outline: "none", boxShadow: "none" },
    });
    const state = EditorState.create({
      doc: "",
      extensions: [
        lineNumbers(),
        highlightActiveLineGutter(),
        highlightActiveLine(),
        noOutline,
      ],
    });

    viewRef.current = new EditorView({
      state,
      parent: editorRef.current,
    });

    return () => {
      viewRef.current?.destroy();
      viewRef.current = null;
    };
  }, []);

  const handleSubmit = async () => {
    const code = viewRef.current?.state.doc.toString();

    if (!code) return;
    console.log(code);

    await axios.post(
      `http://submission.ucode.com/api/v1/submissions`,
      {
        problem_id: props.problemId,
        solution: code,
        language_id: 1,
      },
      {
        headers: {
          Authorization: `Bearer ${keycloakContext.token}`,
        },
      },
    );
  };

  return (
    <div className="flex flex-col overflow-y-auto h-full">
      <div className="flex justify-end h-0 w-full">
        <div className="relative h-fit z-40 right-2 top-2">
          <button
            onClick={() => handleSubmit()}
            className="flex flex-row gap-2 items-center  bg-green text-white font-normal text-sm rounded-3xl px-2.5 py-1.5 hover:cursor-pointer"
          >
            <LiaArrowCircleUpSolid color="var(--color-white)" size={"1rem"} />
            Submit
          </button>
        </div>
      </div>
      <div className="max-h-[40vh] outline-none" ref={editorRef}></div>
    </div>
  );
};

//            onClick={async () => {
//   console.log()
//   await axios.post(
//     "http://submission.ucode.com/api/v1/submissions",
//     {
//       probemId: props.problemId,
//       languageId: 1,
//       solution:
//     }
//     {
//       headers: {
//         Authorization: `Bearer ${keycloakContext.token}`,
//       },
//     },
//   );
// }}
