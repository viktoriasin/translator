package com.vicktoriyasin.translator.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SpellCheckerUnitTest {

    @Test
    void testIsValidForTranslation() {
        Stream.of(
            Arguments.of("", false),
            Arguments.of(" ", false),
            Arguments.of("  ", false),
            Arguments.of("dog", true),
            Arguments.of("a".repeat(100), true),
            Arguments.of("a".repeat(101), false)
        ).forEach(arguments -> assertThat(
            SpellChecker.isValidForTranslation((String) arguments.get()[0])
        ).isEqualTo((Boolean) arguments.get()[1]));
    }
}
