package com.navi.shared.domain;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A value Navi either knows, or explicitly does not know.
 *
 * <p>This type exists because of the product's first core value. When Navi cannot compute something
 * — credit progress with no curriculum on file, a graduation forecast with too little data — the
 * honest answer is "I don't know, and here is why", not {@code 0} and not an empty field. A zero
 * that looks like data is worse than a visible gap, because a student may act on it.
 *
 * <p>Modelled as a sealed interface so that callers are forced by the compiler to handle the
 * unknown case:
 *
 * <pre>{@code
 * String display = switch (progress.creditProgress()) {
 *     case Answer.Known<CreditProgress> k -> format(k.value());
 *     case Answer.Unknown<CreditProgress> u -> "Chưa tính được: " + u.reason();
 * };
 * }</pre>
 *
 * @param <T> the type of the value when it is known
 */
public sealed interface Answer<T> {

    /** Navi knows this value. */
    record Known<T>(T value) implements Answer<T> {
        public Known {
            Objects.requireNonNull(value, "a Known answer cannot hold null — use unknown(reason) instead");
        }
    }

    /**
     * Navi does not know this value.
     *
     * @param reason a message that can be shown to the student, explaining what is missing
     */
    record Unknown<T>(String reason) implements Answer<T> {
        public Unknown {
            if (reason == null || reason.isBlank()) {
                throw new IllegalArgumentException(
                        "An Unknown answer must state why — an unexplained gap is not actionable");
            }
        }
    }

    static <T> Answer<T> known(T value) {
        return new Known<>(value);
    }

    static <T> Answer<T> unknown(String reason) {
        return new Unknown<>(reason);
    }

    default boolean isKnown() {
        return this instanceof Known<T>;
    }

    /**
     * The value if known, otherwise empty. Prefer pattern matching when the reason matters — an
     * {@code Optional} discards the explanation, which is the part a student needs.
     *
     * <p>Named {@code valueIfKnown} rather than {@code value} because {@link Known} already publishes
     * {@code value()} as its record component.
     */
    default Optional<T> valueIfKnown() {
        return this instanceof Known<T>(T value) ? Optional.of(value) : Optional.empty();
    }

    /** Transforms a known value, propagating the reason unchanged when unknown. */
    default <R> Answer<R> map(Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return switch (this) {
            case Known<T>(T value) -> known(mapper.apply(value));
            case Unknown<T>(String reason) -> unknown(reason);
        };
    }
}
