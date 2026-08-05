package com.navi.shared.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GradeTest {

    @Test
    @DisplayName("a grade keeps the scale it was recorded on")
    void keeps_its_scale() {
        Grade grade = Grade.onTenPointScale("8.5");

        assertThat(grade.scale()).isEqualTo(GpaScale.TEN_POINT);
        assertThat(grade.value()).isEqualByComparingTo("8.5");
    }

    @Test
    @DisplayName("a grade outside its scale is rejected")
    void rejects_out_of_range_values() {
        assertThatThrownBy(() -> Grade.onFourPointScale("8.5"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("outside the valid range");

        assertThatThrownBy(() -> Grade.onTenPointScale("10.5"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Grade.onTenPointScale("-0.5"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("a grade without its scale is ambiguous and rejected")
    void requires_a_scale() {
        assertThatThrownBy(() -> new Grade(new BigDecimal("8.5"), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("scale");
    }

    @Test
    @DisplayName("comparing grades across scales fails rather than guessing a conversion")
    void refuses_to_compare_across_scales() {
        Grade tenPoint = Grade.onTenPointScale("8.5");
        Grade fourPoint = Grade.onFourPointScale("3.5");

        // 8.5/10 vs 3.5/4 has no universal answer — conversion rules differ per institution.
        // Guessing one here would corrupt GPA, so the type refuses.
        assertThatThrownBy(() -> tenPoint.compareTo(fourPoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("different scales");
    }

    @Test
    @DisplayName("grades on the same scale compare by value")
    void compares_within_a_scale() {
        assertThat(Grade.onTenPointScale("9.0")).isGreaterThan(Grade.onTenPointScale("8.5"));
    }

    @Test
    @DisplayName("decimal values keep their precision")
    void uses_exact_decimal_arithmetic() {
        // 0.1 + 0.2 in binary floating point is not 0.3; BigDecimal keeps grade maths exact.
        Grade grade = Grade.onTenPointScale("0.3");

        assertThat(grade.value()).isEqualByComparingTo(new BigDecimal("0.1").add(new BigDecimal("0.2")));
    }
}
