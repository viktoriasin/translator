package com.vicktoriyasin.translator.model;

import com.vicktoriyasin.translator.engine.EngineType;
import com.vicktoriyasin.translator.engine.Language;

import java.util.Objects;

public class TranslationRequest {
    public final Language from;
    public final Language to;
    public final String text;
    public final EngineType engineType;

    public TranslationRequest(Language from, Language to, String text, EngineType engineType) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.engineType = engineType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslationRequest that = (TranslationRequest) o;
        return from == that.from && to == that.to && text.equals(that.text) && engineType == that.engineType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, text, engineType);
    }
}
