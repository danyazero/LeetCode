ALTER TABLE "public"."submission" ADD COLUMN "idempotency_key" uuid NOT NULL;
ALTER TABLE "public"."submission" ALTER COLUMN "solution_path" DROP NOT NULL;
