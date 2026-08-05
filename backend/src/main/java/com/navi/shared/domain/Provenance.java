package com.navi.shared.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Where a piece of knowledge came from and how far it has been verified.
 *
 * <p>Every knowledge entity in Navi (curriculum, course, skill, roadmap, and later role
 * requirements) carries one of these. This is the type-level expression of the product's first
 * core value: no unverified information reaches the student unlabelled.
 *
 * @param sourceId the source this knowledge was taken from — never null
 * @param status   how far it has been verified — never null, no default
 * @param verifiedAt when verification happened; null unless {@code status} is
 *                   {@link VerificationStatus#VERIFIED}
 * @param verifiedBy who or which process verified it; null unless verified
 */
public record Provenance(
        UUID sourceId,
        VerificationStatus status,
        Instant verifiedAt,
        String verifiedBy
) {

    public Provenance {
        Objects.requireNonNull(sourceId, "sourceId is required — knowledge without a source is not allowed");
        Objects.requireNonNull(status, "verification status is required and has no default");

        // A record claiming VERIFIED without saying when and by whom is not actually verifiable,
        // so reject it here rather than letting it reach the database.
        if (status == VerificationStatus.VERIFIED) {
            Objects.requireNonNull(verifiedAt, "verifiedAt is required when status is VERIFIED");
            if (verifiedBy == null || verifiedBy.isBlank()) {
                throw new IllegalArgumentException("verifiedBy is required when status is VERIFIED");
            }
        }
    }

    /** Knowledge taken from an official source that has been checked. */
    public static Provenance verified(UUID sourceId, Instant verifiedAt, String verifiedBy) {
        return new Provenance(sourceId, VerificationStatus.VERIFIED, verifiedAt, verifiedBy);
    }

    /** Knowledge contributed and reviewed by the community. */
    public static Provenance community(UUID sourceId) {
        return new Provenance(sourceId, VerificationStatus.COMMUNITY, null, null);
    }

    /** Knowledge whose accuracy nobody has checked yet. */
    public static Provenance unverified(UUID sourceId) {
        return new Provenance(sourceId, VerificationStatus.UNVERIFIED, null, null);
    }

    public Optional<Instant> verifiedAtOptional() {
        return Optional.ofNullable(verifiedAt);
    }

    public boolean presentableAsFact() {
        return status.presentableAsFact();
    }
}
