package com.navi.shared.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreditTest {

    @ParameterizedTest
    @ValueSource(ints = {-1, -3, Integer.MIN_VALUE})
    @DisplayName("credits are never negative")
    void rejects_negative_values(int invalid) {
        assertThatThrownBy(() -> Credit.of(invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be negative");
    }

    @Test
    @DisplayName("credits add up across courses")
    void sums_a_collection() {
        Credit total = Credit.sum(List.of(Credit.of(3), Credit.of(4), Credit.of(2)));

        assertThat(total).isEqualTo(Credit.of(9));
    }

    @Test
    @DisplayName("summing nothing gives zero, not an error")
    void sums_an_empty_collection_to_zero() {
        assertThat(Credit.sum(List.of())).isEqualTo(Credit.ZERO);
    }

    @Test
    @DisplayName("subtraction cannot produce a negative credit total")
    void rejects_subtraction_below_zero() {
        assertThatThrownBy(() -> Credit.of(3).minus(Credit.of(5)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("overflow fails loudly instead of wrapping to a negative total")
    void detects_overflow() {
        // A wrapped total would surface as a nonsensical progress figure, which is exactly the kind
        // of silently wrong number the Trust value forbids.
        assertThatThrownBy(() -> Credit.of(Integer.MAX_VALUE).plus(Credit.of(1)))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    @DisplayName("credits compare by value")
    void compares_by_value() {
        assertThat(Credit.of(4).isGreaterThan(Credit.of(3))).isTrue();
        assertThat(Credit.of(3).isGreaterThan(Credit.of(3))).isFalse();
        assertThat(Credit.ZERO.isZero()).isTrue();
    }

    @Test
    @DisplayName("toString reads naturally in logs and messages")
    void formats_readably() {
        assertThat(Credit.of(1)).hasToString("1 credit");
        assertThat(Credit.of(3)).hasToString("3 credits");
    }
}
