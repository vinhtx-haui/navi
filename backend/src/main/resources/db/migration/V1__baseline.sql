-- V1__baseline.sql
--
-- Baseline schema for Navi Platform.
--
-- This migration establishes two things that every later migration depends on:
--   1. One schema per module, so module boundaries exist at the database level too (ADR-0001).
--   2. knowledge.sources, which every knowledge-bearing table must reference. No table holding
--      curriculum, course, skill or roadmap data may exist without pointing at a source.
--
-- See docs/architecture.md §5 and docs/adr/0003-postgresql-as-primary-datastore.md.

-- ─── Module schemas ──────────────────────────────────────────────────────────
CREATE SCHEMA IF NOT EXISTS identity;
CREATE SCHEMA IF NOT EXISTS academic;
CREATE SCHEMA IF NOT EXISTS progress;
CREATE SCHEMA IF NOT EXISTS goal;
CREATE SCHEMA IF NOT EXISTS skill;
CREATE SCHEMA IF NOT EXISTS knowledge;

COMMENT ON SCHEMA identity  IS 'Users, authentication, sessions';
COMMENT ON SCHEMA academic  IS 'Curricula, courses, enrollments, semesters';
COMMENT ON SCHEMA progress  IS 'Computed progress snapshots';
COMMENT ON SCHEMA goal      IS 'Goals and subgoals';
COMMENT ON SCHEMA skill     IS 'Skills, roadmaps, proficiency';
COMMENT ON SCHEMA knowledge IS 'Sources and verification — the basis of the Trust core value';

-- ─── Shared updated_at trigger ───────────────────────────────────────────────
-- Kept in public so every module schema can use it. Setting updated_at in the database means an
-- application code path that forgets to set it cannot produce a misleading timestamp.
CREATE OR REPLACE FUNCTION public.set_updated_at()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$;

COMMENT ON FUNCTION public.set_updated_at() IS
    'Trigger function: sets updated_at to now() on UPDATE. Attach to every mutable table.';

-- ─── knowledge.sources ───────────────────────────────────────────────────────
-- The origin of a piece of knowledge. Referenced by every knowledge-bearing table.
CREATE TABLE knowledge.sources
(
    id           UUID         PRIMARY KEY,

    -- What kind of origin this is. Constrained rather than free text so that reporting on
    -- "how much of our data is official" stays possible.
    kind         VARCHAR(32)  NOT NULL
        CONSTRAINT sources_kind_valid CHECK (kind IN (
            'UNIVERSITY_OFFICIAL',  -- curriculum published by an institution
            'JOB_POSTING',          -- a real job advertisement (Phase 4)
            'COMMUNITY_CONTRIBUTION',
            'EDITORIAL',            -- written by the Navi team
            'OTHER'
        )),

    name         VARCHAR(255) NOT NULL,

    -- Where a human can go to check this claim. Nullable only because some sources are offline
    -- documents; when it is null, `note` must explain how to verify.
    reference_url TEXT        NULL,
    note         TEXT         NULL,

    -- When the underlying source material was published or last updated at the origin.
    -- Career and curriculum data goes stale, so this is required for freshness checks.
    published_at TIMESTAMPTZ  NULL,

    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at   TIMESTAMPTZ  NULL,

    CONSTRAINT sources_verifiable CHECK (reference_url IS NOT NULL OR note IS NOT NULL)
);

COMMENT ON TABLE knowledge.sources IS
    'Origin of every piece of knowledge in Navi. A knowledge row without a source is not allowed.';
COMMENT ON CONSTRAINT sources_verifiable ON knowledge.sources IS
    'A source must be checkable: either a URL, or a note explaining how to verify it offline.';

CREATE INDEX idx_sources_kind ON knowledge.sources (kind) WHERE deleted_at IS NULL;

CREATE TRIGGER trg_sources_updated_at
    BEFORE UPDATE ON knowledge.sources
    FOR EACH ROW
EXECUTE FUNCTION public.set_updated_at();

-- ─── Convention reminder for later migrations ────────────────────────────────
-- Every table that carries knowledge (curricula, courses, skills, roadmaps, role_requirements)
-- MUST include:
--
--   source_id           UUID         NOT NULL REFERENCES knowledge.sources (id),
--   verification_status VARCHAR(20)  NOT NULL
--       CHECK (verification_status IN ('VERIFIED', 'COMMUNITY', 'UNVERIFIED')),
--   verified_at         TIMESTAMPTZ  NULL,
--   verified_by         VARCHAR(255) NULL
--
-- verification_status must have NO DEFAULT. Forgetting to record provenance has to fail at write
-- time; a row that defaults to VERIFIED is a silent lie to the student.
