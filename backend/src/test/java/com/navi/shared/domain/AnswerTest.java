package com.navi.shared.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AnswerTest {

    @Test
    @DisplayName("a known answer carries its value")
    void known_carries_value() {
        Answer<Integer> answer = Answer.known(78);

        assertThat(answer.isKnown()).isTrue();
        assertThat(answer.valueIfKnown()).contains(78);
    }

    @Test
    @DisplayName("an unknown answer must explain what is missing")
    void unknown_requires_a_reason() {
        assertThatThrownBy(() -> Answer.unknown("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must state why");
    }

    @Test
    @DisplayName("a known answer cannot hold null — that would be an unexplained gap")
    void known_rejects_null() {
        assertThatThrownBy(() -> Answer.known(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("mapping preserves the reason instead of inventing a value")
    void map_propagates_the_reason() {
        Answer<Integer> unknown = Answer.unknown("Chưa có chương trình đào tạo cho ngành này");

        Answer<String> mapped = unknown.map(String::valueOf);

        assertThat(mapped.isKnown()).isFalse();
        assertThat(mapped).isInstanceOf(Answer.Unknown.class);
        assertThat(((Answer.Unknown<String>) mapped).reason())
                .isEqualTo("Chưa có chương trình đào tạo cho ngành này");
    }

    @Test
    @DisplayName("callers are forced by the compiler to handle the unknown case")
    void pattern_matching_covers_both_cases() {
        Answer<Integer> answer = Answer.unknown("not enough data");

        // The switch has no default branch: if a third variant were ever added, this stops compiling
        // rather than silently falling through — which is the reason Answer is sealed.
        String rendered = switch (answer) {
            case Answer.Known<Integer> known -> String.valueOf(known.value());
            case Answer.Unknown<Integer> unknown -> "unknown: " + unknown.reason();
        };

        assertThat(rendered).isEqualTo("unknown: not enough data");
    }
}
