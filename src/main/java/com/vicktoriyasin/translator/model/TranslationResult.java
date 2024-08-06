package com.vicktoriyasin.translator.model;

public class TranslationResult {
    public final String text;
    public final TranslationError error;

    public TranslationResult(String text, TranslationError error) {
        this.text = text;
        this.error = error;
    }

    public TranslationResult(String text) {
        this(text, null);
    }

    public TranslationResult(TranslationError error) {
        this(null, error);
    }

    public int responseCode() {
        if (error == null) {
            return 200;
        } else {
            return error.statusCode;
        }
    }

    public String errorMessage() {
        if (error != null) {
            return error.message;
        } else {
            return null;
        }
    }
}
