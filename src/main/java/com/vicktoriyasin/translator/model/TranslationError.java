package com.vicktoriyasin.translator.model;

public enum TranslationError {
    NO_TEXT_TO_TRANSLATE(400, "The 'text' for translation is not specified"),
    NO_LANGUAGE_TO(400, "The translation language 'to' is not specified"),
    WRONG_LANGUAGE_TO(400, "The translation language 'to' cannot be recognized"),
    WRONG_LANGUAGE_FROM(400, "The translation language 'from' cannot be recognized"),
    NETWORK_ERROR(400, "Network error while connecting to the translator"),
    ENGINE_ERROR(400, "Error while processing the translator's response"),
    YANDEX_AUTH_ERROR(500, "The token for accessing the Yandex translator is not specified on the server"),
    ;

    public final int statusCode;
    public final String message;

    TranslationError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
