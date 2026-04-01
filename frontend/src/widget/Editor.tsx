import { EditorState } from "@codemirror/state";
import {
  EditorView,
  highlightActiveLine,
  highlightActiveLineGutter,
  lineNumbers,
} from "@codemirror/view";
import { useEffect, useRef } from "react";
import { useProblemStore } from "@/features/Problem/store/useProblemStore";

export interface EditorProps {
  problemId: number;
}

export const Editor = (props: EditorProps) => {
  const editorRef = useRef<HTMLDivElement>(null);
  const viewRef = useRef<EditorView | null>(null);
  const code = useProblemStore((state) => state.code);
  const setCode = useProblemStore((state) => state.setCode);

  useEffect(() => {
    if (!editorRef.current) return;
    const theme = EditorView.theme({
      "&": { outline: "none", height: "100%" },
      "&.cm-focused": { outline: "none", boxShadow: "none" },
      ".cm-scroller": { overflow: "auto" },
    });

    const updateListener = EditorView.updateListener.of((update) => {
      if (update.docChanged) {
        setCode(update.state.doc.toString());
      }
    });

    const state = EditorState.create({
      doc: "",
      extensions: [
        lineNumbers(),
        highlightActiveLineGutter(),
        highlightActiveLine(),
        theme,
        updateListener,
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
  }, [setCode]);

  useEffect(() => {
    const view = viewRef.current;

    if (!view) return;

    const currentCode = view.state.doc.toString();
    if (currentCode === code) return;

    view.dispatch({
      changes: {
        from: 0,
        to: currentCode.length,
        insert: code,
      },
    });
  }, [code]);

  return <div className="h-full overflow-auto" ref={editorRef}></div>;
};
