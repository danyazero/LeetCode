import { EditorState } from "@codemirror/state";
import {
  EditorView,
  highlightActiveLine,
  highlightActiveLineGutter,
  lineNumbers,
} from "@codemirror/view";
import { forwardRef, useEffect, useImperativeHandle, useRef } from "react";
import axios from "axios";
import { keycloakContext } from "@/features/KeycloakWrapper";

export interface EditorProps {
  problemId: number;
}

export interface EditorHandle {
  submit: () => void;
}

export const Editor = forwardRef<EditorHandle, EditorProps>((props, ref) => {
  const editorRef = useRef<HTMLDivElement>(null);
  const viewRef = useRef<EditorView | null>(null);

  useEffect(() => {
    if (!editorRef.current) return;
    const theme = EditorView.theme({
      "&": { outline: "none", height: "100%" },
      "&.cm-focused": { outline: "none", boxShadow: "none" },
      ".cm-scroller": { overflow: "auto" },
    });
    const state = EditorState.create({
      doc: "",
      extensions: [
        lineNumbers(),
        highlightActiveLineGutter(),
        highlightActiveLine(),
        theme,
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
      `http://submission.localhost/api/v1/submissions`,
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

  useImperativeHandle(ref, () => ({
    submit: handleSubmit,
  }));

  return (
    <div className="h-full overflow-auto" ref={editorRef}></div>
  );
});