ALTER TABLE "public"."testcase"
    DROP COLUMN "storage_path",
    ADD COLUMN "input" text NOT NULL,
    ADD COLUMN "output" text NOT NULL;