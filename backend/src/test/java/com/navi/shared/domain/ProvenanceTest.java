package com.navi.shared.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Provenance is where the Trust core value is enforced in code, so these tests are about what the
 * type refuses to build, not just what it builds.
 */
class ProvenanceTest {

    private static final UUID SOURCE = Ids.newId();
    private static final Instant WHEN = Instant.parse("2026-07-01T00:00:00Z");

    @Nested
    @DisplayName("a record claiming VERIFIED")
    class VerifiedClaims {

        @Test
        @DisplayName("is accepted when it says when and by whom")
        void accepts_a_complete_verification() {
            Provenance provenance = Provenance.verified(SOURCE, WHEN, "curriculum-team");

            assertThat(provenance.status()).isEqualTo(VerificationStatus.VERIFIED);
            assertThat(provenance.presentableAsFact()).isTrue();
            assertThat(provenance.verifiedAtOptional()).contains(WHEN);
        }

        @Test
        @DisplayName("is rejected without a verification time")
        void rejects_missing_verified_at() {
            assertThatThrownBy(() -> new Provenance(SOURCE, VerificationStatus.VERIFIED, null, "someone"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("verifiedAt");
        }

        @Test
        @DisplayName("is rejected without a verifier")
        void rejects_blank_verified_by() {
            assertThatThrownBy(() -> new Provenance(SOURCE, VerificationStatus.VERIFIED, WHEN, "  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("verifiedBy");
        }
    }

    @Test
    @DisplayName("knowledge without a source cannot be constructed at all")
    void rejects_missing_source() {
        assertThatThrownBy(() -> Provenance.unverified(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("sourceId");
    }

    @Test
    @DisplayName("verification status has no default — it must be stated")
    void rejects_missing_status() {
        assertThatThrownBy(() -> new Provenance(SOURCE, null, null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("status");
    }

    @Test
    @DisplayName("community and unverified data is not presentable as fact")
    void unverified_data_is_not_presentable_as_fact() {
        assertThat(Provenance.community(SOURCE).presentableAsFact()).isFalse();
        assertThat(Provenance.unverified(SOURCE).presentableAsFact()).isFalse();
        assertThat(Provenance.unverified(SOURCE).status().requiresWarning()).isTrue();
        assertThat(Provenance.community(SOURCE).status().requiresWarning()).isFalse();
    }
}
