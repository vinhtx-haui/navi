package com.navi.shared.domain;

import java.math.BigDecimal;

/**
 * A grading scale.
 *
 * <p>Stored explicitly with every grade. Vietnamese universities commonly use a 10-point scale for
 * individual courses and a 4-point scale for GPA, and conversion rules differ between institutions.
 * Recording the original scale means Navi never has to guess which scale a stored number was on —
 * guessing here would silently corrupt the one figure students trust most.
 */
public enum GpaScale {

    /** 0.0–4.0, typically used for cumulative GPA. */
    FOUR_POINT(new BigDecimal("0.0"), new BigDecimal("4.0")),

    /** 0.0–10.0, typically used for individual course grades in Vietnam. */
    TEN_POINT(new BigDecimal("0.0"), new BigDecimal("10.0"));

    private final BigDecimal min;
    private final BigDecimal max;

    GpaScale(BigDecimal min, BigDecimal max) {
        this.min = min;
        this.max = max;
    }

    public BigDecimal min() {
        return min;
    }

    public BigDecimal max() {
        return max;
    }

    public boolean contains(BigDecimal value) {
        return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }
}
