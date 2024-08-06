package com.vicktoriyasin.translator.service;

import com.vicktoriyasin.translator.engine.Language;
import com.vicktoriyasin.translator.engine.TranslationEngine;
import com.vicktoriyasin.translator.jdbc.JdbcClient;
import com.vicktoriyasin.translator.model.TranslationError;
import com.vicktoriyasin.translator.model.TranslationRequest;
import com.vicktoriyasin.translator.model.TranslationResult;
import com.vicktoriyasin.translator.util.CacheInMemory;
import com.vicktoriyasin.translator.util.Log;
import com.vicktoriyasin.translator.util.SpellChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.vicktoriyasin.translator.model.TranslationError.*;
import static org.springframework.util.StringUtils.isEmpty;

@Service
public class TranslationService {

    private final static int PARALLELISM = 10;

    @Autowired
    private TranslationEngine translationEngine;

    @Autowired
    private JdbcClient jdbcClient;

    private final Executor executor = Executors.newFixedThreadPool(PARALLELISM);

    private final CacheInMemory cacheInMemory = new CacheInMemory(1000);

    public TranslationResult translate(String fromRaw, String toRaw, String text) {
        final Language from = isEmpty(fromRaw) ? Language.AUTO : Language.parse(fromRaw);
        if (from == null) {
            Log.info("Failed to parse 'from' language: '" + fromRaw + "'");
            return new TranslationResult(WRONG_LANGUAGE_FROM);
        }

        if (isEmpty(toRaw)) {
            Log.info("No 'to' language");
            return new TranslationResult(NO_LANGUAGE_TO);
        }

        final Language to = Language.parse(toRaw);
        if (to == null) {
            Log.info("Failed to parse 'to' language: '" + toRaw + "'");
            return new TranslationResult(WRONG_LANGUAGE_TO);
        }

        if (text == null) {
            Log.info("No text to translate");
            return new TranslationResult(NO_TEXT_TO_TRANSLATE);
        }

        List<CompletableFuture<TranslationResult>> futureList = Arrays.stream(text.split(" "))
            .map(word -> CompletableFuture.supplyAsync(() -> translate(from, to, word), executor))
            .collect(Collectors.toList());

        TranslationResultAccumulator accumulator = new TranslationResultAccumulator();
        futureList.forEach(accumulator::collect);
        return accumulator.buildTranslationResult();
    }

    private TranslationResult translate(Language from, Language to, String word) {
        if (!SpellChecker.isValidForTranslation(word)) {
            Log.info("Word '" + word + "' need not be translated");
            return new TranslationResult(word);
        }

        TranslationRequest translationRequest = new TranslationRequest(from, to, word, translationEngine.engineType());

        String translatedFromCacheInMemory = cacheInMemory.get(translationRequest);
        if (translatedFromCacheInMemory != null) {
            log(from, to, word, translatedFromCacheInMemory, "Memory cache");
            return new TranslationResult(translatedFromCacheInMemory);
        }

        String translatedFromCacheInDb = jdbcClient.getTranslatedWord(translationRequest);
        if (translatedFromCacheInDb != null) {
            log(from, to, word, translatedFromCacheInDb, "DB cache");
            cacheInMemory.put(translationRequest, translatedFromCacheInDb);
            return new TranslationResult(translatedFromCacheInDb);
        }

        TranslationResult translatedFromEngine = translationEngine.translate(from, to, word);
        if (translatedFromEngine.text != null) {
            log(from, to, word, translatedFromEngine.text, "TranslationEngine");
            jdbcClient.saveTranslatedWord(translationRequest, translatedFromEngine.text);
            cacheInMemory.put(translationRequest, translatedFromEngine.text);
        }

        return translatedFromEngine;
    }

    private void log(Language from, Language to, String wordFrom, String wordTo, String translator) {
        Log.info("Translate [" + from.code + " -> " + to.code + "] '" + wordFrom + "' -> '" + wordTo + "' using " + translator);
    }

    private static class TranslationResultAccumulator {
        final StringBuilder sb = new StringBuilder();
        boolean isCancelled = false;
        TranslationError error = null;

        void collect(CompletableFuture<TranslationResult> future) {
            if (isCancelled) {
                future.cancel(true);
                return;
            }

            TranslationResult result;
            try {
                result = future.get();
            } catch (Exception ignored) {
                return;
            }

            if (result.text == null) {
                isCancelled = true;
                error = result.error;
            } else {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(result.text);
            }
        }

        TranslationResult buildTranslationResult() {
            if (isCancelled) {
                return new TranslationResult(error);
            } else {
                return new TranslationResult(sb.toString());
            }
        }
    }
}
