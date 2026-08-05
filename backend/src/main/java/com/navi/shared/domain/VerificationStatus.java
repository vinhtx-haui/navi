package com.navi.shared.domain;

/**
 * How far a piece of knowledge has been verified.
 *
 * <p>There is deliberately no default value. Every knowledge record must state its status at the
 * moment it is written, so that "forgot to record the source" fails immediately instead of quietly
 * producing a record that looks trustworthy. See {@code docs/adr/0003-postgresql-as-primary-datastore.md}.
 */
public enum VerificationStatus {

    /** Checked against an official source, with a recorded time and verifier. */
    VERIFIED,

    /** Contributed and reviewed by the community, but not an official source. */
    COMMUNITY,

    /** Not verified. Must never be presented alongside verified data without a warning. */
    UNVERIFIED;

    /**
     * Whether this status may be presented to a student as established fact.
     *
     * <p>Callers use this to decide presentation, but the decision lives here so that it cannot
     * drift between call sites.
     */
    public boolean presentableAsFact() {
        return this == VERIFIED;
    }

    /** Whether a visible warning is required when showing data with this status. */
    public boolean requiresWarning() {
        return this == UNVERIFIED;
    }
}
