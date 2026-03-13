import { create } from "zustand";
import type { SuggestionItem } from "@/shared/SuggestionsDropdown";

export type SearchToken = SuggestionItem; // SuggestionItem already has kind: "tag" | "difficulty" and a data object

interface SearchState {
  rawInput: string;
  activeIndex: number;
  dropdownOpen: boolean;
  tokens: SearchToken[];

  // Actions
  setRawInput: (input: string) => void;
  setActiveIndex: (index: number | ((prev: number) => number)) => void;
  setDropdownOpen: (open: boolean) => void;
  addToken: (token: SearchToken) => void;
  removeToken: (id: string, kind: "tag" | "difficulty") => void;
  clearTokens: () => void;
  applySuggestion: (item: SuggestionItem, activeKind: "tag" | "difficulty" | null) => void;
}

export const useSearchStore = create<SearchState>((set, get) => ({
  rawInput: "",
  activeIndex: -1,
  dropdownOpen: false,
  tokens: [],

  setRawInput: (input) => set({ rawInput: input }),

  setActiveIndex: (index) => set((state) => ({
    activeIndex: typeof index === "function" ? index(state.activeIndex) : index
  })),

  setDropdownOpen: (open) => set({ dropdownOpen: open }),

  addToken: (token) => set((state) => ({
    tokens: [...state.tokens.filter(t => t.kind !== token.kind), token]
  })),

  removeToken: (id, kind) => set((state) => ({
    tokens: state.tokens.filter(t => !(t.data.id.toString() === id && t.kind === kind))
  })),

  clearTokens: () => set({ tokens: [] }),

  applySuggestion: (item, activeKind) => {
    if (!activeKind) return;

    get().addToken(item);

    set((state) => ({
      rawInput: state.rawInput.replace(/(?:^|[\s])(?:@|#)\S*$/, "").trimEnd(),
      dropdownOpen: false
    }));
  }
}));
