package com.vicktoriyasin.translator.util;

public class SpellChecker {

    private static final int MAX_WORD_LENGTH = 100;

    public static boolean isValidForTranslation(String word) {
        return word.length() <= MAX_WORD_LENGTH && !word.isBlank();
    }
}
