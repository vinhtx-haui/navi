package com.navi.shared.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A grade, together with the scale it was recorded on.
 *
 * <p>Navi stores the grade exactly as the university reported it. Conversion between scales is an
 * explicit operation, never implicit: a bare {@code 8.5} is meaningless without knowing whether the
 * scale is 10-point or 4-point, and a wrong assumption here produces a wrong GPA — the number a
 * student is most likely to act on.
 *
 * <p>{@link BigDecimal} rather than {@code double}: grade values are decimal quantities that get
 * compared and aggregated, and binary floating point makes those comparisons unreliable.
 */
public record Grade(BigDecimal value, GpaScale scale) implements Comparable<Grade> {

    public Grade {
        Objects.requireNonNull(value, "grade value is required");
        Objects.requireNonNull(scale, "grade scale is required — a grade without its scale is ambiguous");

        if (!scale.contains(value)) {
            throw new IllegalArgumentException(
                    "Grade " + value + " is outside the valid range of scale " + scale
                            + " (" + scale.min() + "–" + scale.max() + ")");
        }
    }

    public static Grade onTenPointScale(String value) {
        return new Grade(new BigDecimal(value), GpaScale.TEN_POINT);
    }

    public static Grade onFourPointScale(String value) {
        return new Grade(new BigDecimal(value), GpaScale.FOUR_POINT);
    }

    /**
     * Compares two grades.
     *
     * @throws IllegalArgumentException if the grades are on different scales — comparing across
     *                                  scales requires an explicit, institution-specific conversion
     *                                  rule that this type does not have
     */
    @Override
    public int compareTo(Grade other) {
        if (this.scale != other.scale) {
            throw new IllegalArgumentException(
                    "Cannot compare grades on different scales (" + this.scale + " vs " + other.scale
                            + ") — convert explicitly using the institution's conversion rule first");
        }
        return this.value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value + "/" + scale.max();
    }
}
