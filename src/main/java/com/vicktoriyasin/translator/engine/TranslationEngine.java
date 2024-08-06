package com.vicktoriyasin.translator.engine;

import com.vicktoriyasin.translator.model.TranslationResult;

public interface TranslationEngine {
    EngineType engineType();
    TranslationResult translate(Language from, Language to, String text);
}
