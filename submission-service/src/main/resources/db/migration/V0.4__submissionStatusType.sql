ALTER TABLE "public"."submission"
DROP
COLUMN "status",
  ADD COLUMN "status" integer NOT NULL;

ALTER TABLE "public"."events"
DROP
COLUMN "status",
  ADD COLUMN "status" integer NOT NULL;

ALTER TABLE "public"."submission" RENAME COLUMN "solution_path" TO "solution";
ALTER TABLE "public"."submission" DROP COLUMN "idempotency_key";