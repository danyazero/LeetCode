import { Badge, type Variant } from "@/shared/Badge";
import { Skeleton } from "@/components/ui/skeleton";
import type { Tag } from "@/api/problems";

interface ProblemTagsProps {
  tags?: Tag[] | null;
}

export function ProblemTags({ tags }: ProblemTagsProps) {
  return (
    <div className="flex flex-wrap gap-2">
      {!tags ? (
        <>
          <Skeleton className="h-[22px] w-16 rounded-full" />
          <Skeleton className="h-[22px] w-20 rounded-full" />
          <Skeleton className="h-[22px] w-14 rounded-full" />
        </>
      ) : tags.length === 0 ? (
        <span className="text-xs text-muted-foreground">No tags</span>
      ) : (
        tags.map((tag) => (
          <Badge key={tag.id} title={tag.value} variant={"default" as Variant} />
        ))
      )}
    </div>
  );
}
