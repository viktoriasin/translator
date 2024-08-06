package com.vicktoriyasin.translator.util;

import com.vicktoriyasin.translator.model.TranslationRequest;

import java.util.Collections;
import java.util.Map;

public class CacheInMemory {
    private final Map<TranslationRequest, String> cache;

    public CacheInMemory(int maxEntries) {
        this.cache = Collections.synchronizedMap(new LruCache<>(maxEntries));
    }

    public String get(TranslationRequest request) {
        return cache.get(request);
    }

    public void put(TranslationRequest request, String translationResult) {
        cache.put(request, translationResult);
    }
}
