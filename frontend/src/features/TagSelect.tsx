"use client";

import * as React from "react";
import { Check, ChevronsUpDown, Loader2, X } from "lucide-react";

import { Button } from "@/components/ui/button";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { cn } from "@/lib/utils";
import { fetchAllTags, fetchTagsByQuery } from "@/api/problems";
import { useDebounce } from "@/hooks/useDebounce";
import type { Tag } from "@/shared/SuggestionsDropdown";

interface TagSelectProps {
  value: number | undefined;
  onChange: (tagId: number | undefined) => void;
}

export function TagSelect({ value, onChange }: TagSelectProps) {
  const [open, setOpen] = React.useState(false);
  const [search, setSearch] = React.useState("");
  const [tags, setTags] = React.useState<Tag[]>([]);
  const [loading, setLoading] = React.useState(false);

  const debouncedSearch = useDebounce(search, 350);

  // Fetch tags whenever the debounced search term changes
  React.useEffect(() => {
    let cancelled = false;
    setLoading(true);

    const fetch = debouncedSearch.trim()
      ? fetchTagsByQuery(debouncedSearch.trim())
      : fetchAllTags();

    fetch
      .then((page) => {
        if (!cancelled) setTags(page.content);
      })
      .catch(console.error)
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [debouncedSearch]);

  const selectedTag = tags.find((t) => t.id === value);

  const handleSelect = (tagId: number) => {
    onChange(tagId === value ? undefined : tagId);
    setOpen(false);
  };

  const handleClear = (e: React.MouseEvent) => {
    e.stopPropagation();
    onChange(undefined);
  };

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          role="combobox"
          aria-expanded={open}
          className="w-full justify-between font-normal text-sm h-9"
        >
          <span
            className={cn("truncate", !selectedTag && "text-muted-foreground")}
          >
            {selectedTag ? selectedTag.value : "Filter by tag…"}
          </span>

          <span className="flex items-center gap-0.5 ml-2 shrink-0">
            {selectedTag && (
              <span
                role="button"
                aria-label="Clear tag"
                onClick={handleClear}
                className="rounded-sm p-0.5 hover:bg-muted transition-colors"
              >
                <X size={12} />
              </span>
            )}
            <ChevronsUpDown size={13} className="text-muted-foreground" />
          </span>
        </Button>
      </PopoverTrigger>

      <PopoverContent className="w-64 p-0" align="start">
        <Command shouldFilter={false}>
          <CommandInput
            placeholder="Search tags…"
            value={search}
            onValueChange={setSearch}
          />

          <CommandList>
            {loading ? (
              <div className="flex items-center justify-center py-6 gap-2 text-xs text-muted-foreground">
                <Loader2 size={14} className="animate-spin" />
                Loading…
              </div>
            ) : (
              <>
                <CommandEmpty>No tags found.</CommandEmpty>
                <CommandGroup>
                  {tags.map((tag) => (
                    <CommandItem
                      key={tag.id}
                      value={String(tag.id)}
                      onSelect={() => handleSelect(tag.id)}
                      className="cursor-pointer"
                    >
                      <Check
                        size={13}
                        className={cn(
                          "mr-2 shrink-0",
                          value === tag.id ? "opacity-100" : "opacity-0",
                        )}
                      />
                      {tag.value}
                    </CommandItem>
                  ))}
                </CommandGroup>
              </>
            )}
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  );
}
