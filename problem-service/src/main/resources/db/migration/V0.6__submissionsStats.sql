ALTER TABLE "public"."problem"
    ADD COLUMN "sent_submissions" integer NOT NULL DEFAULT '0',
  ADD COLUMN "accepted_submissions" integer NOT NULL DEFAULT '0';

ALTER TABLE "public"."problem" DROP COLUMN "method_schema";