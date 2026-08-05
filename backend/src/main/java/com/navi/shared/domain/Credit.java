package com.navi.shared.domain;

import java.util.Objects;

/**
 * A number of academic credits (tín chỉ).
 *
 * <p>A value object rather than a bare {@code int}, so that "3 credits" cannot be accidentally
 * passed where "3 courses" or "3 semesters" was meant. Credit arithmetic runs through progress
 * calculation, which produces the numbers students use to make decisions.
 *
 * <p>This type enforces only what is true of credits everywhere: they are never negative. Rules
 * such as "a single course has at most N credits" belong to the {@code academic} module, which
 * knows what a course is — not here.
 */
public record Credit(int value) implements Comparable<Credit> {

    public static final Credit ZERO = new Credit(0);

    public Credit {
        if (value < 0) {
            throw new IllegalArgumentException("Credit cannot be negative: " + value);
        }
    }

    public static Credit of(int value) {
        return new Credit(value);
    }

    public Credit plus(Credit other) {
        Objects.requireNonNull(other, "other");
        return new Credit(Math.addExact(this.value, other.value));
    }

    public Credit minus(Credit other) {
        Objects.requireNonNull(other, "other");
        return new Credit(Math.subtractExact(this.value, other.value));
    }

    /** Sum of a collection of credits; {@link #ZERO} for an empty collection. */
    public static Credit sum(Iterable<Credit> credits) {
        Objects.requireNonNull(credits, "credits");
        Credit total = ZERO;
        for (Credit credit : credits) {
            total = total.plus(credit);
        }
        return total;
    }

    public boolean isZero() {
        return value == 0;
    }

    public boolean isGreaterThan(Credit other) {
        return compareTo(other) > 0;
    }

    @Override
    public int compareTo(Credit other) {
        return Integer.compare(this.value, other.value);
    }

    @Override
    public String toString() {
        return value + " credit" + (value == 1 ? "" : "s");
    }
}
