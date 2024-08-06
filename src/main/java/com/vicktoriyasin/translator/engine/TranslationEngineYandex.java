package com.vicktoriyasin.translator.engine;

import com.google.gson.JsonParser;
import com.vicktoriyasin.translator.model.TranslationResult;
import com.vicktoriyasin.translator.util.Log;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static com.vicktoriyasin.translator.model.TranslationError.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class TranslationEngineYandex implements TranslationEngine {

    private final String yandexApiKey;
    private final RestTemplate restTemplate;

    public TranslationEngineYandex(String yandexApiKey, RestTemplate restTemplate) {
        this.yandexApiKey = yandexApiKey;
        this.restTemplate = restTemplate;
    }

    @Override
    public EngineType engineType() {
        return EngineType.YANDEX;
    }

    public TranslationResult translate(Language from, Language to, String text) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(createUri(), createRequest(from, to, text), String.class);

            String body = response.getBody();
            if (body == null) {
                Log.error("Engine error in TranslationEngineYandex for '" + text + "'");
                return new TranslationResult(ENGINE_ERROR);
            }

            String translatedText = JsonParser.parseString(body)
                .getAsJsonObject().get("translations")
                .getAsJsonArray().get(0)
                .getAsJsonObject().get("text")
                .getAsString();

            return new TranslationResult(translatedText);

        } catch (ResourceAccessException e) {
            Log.error("Network error in TranslationEngineYandex for '" + text + "'");
            return new TranslationResult(NETWORK_ERROR);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                Log.error("TRANSLATION_ENGINE_YANDEX_API_KEY is incorrect");
                return new TranslationResult(YANDEX_AUTH_ERROR);
            } else {
                Log.error("Engine error in TranslationEngineYandex for '" + text + "'");
                return new TranslationResult(ENGINE_ERROR);
            }
        } catch (Exception e) {
            Log.error("Engine error in TranslationEngineYandex for '" + text + "'");
            return new TranslationResult(ENGINE_ERROR);
        }
    }

    private String createUri() {
        return "https://translate.api.cloud.yandex.net/translate/v2/translate";
    }

    private HttpEntity<String> createRequest(Language from, Language to, String text) {
        StringBuilder sb = new StringBuilder("{");
        if (from != Language.AUTO) {
            sb.append("'sourceLanguageCode':'").append(from.code).append("',");
        }
        sb.append("'targetLanguageCode':'").append(to.code).append("',");
        sb.append("'texts':['").append(text.replace("'", "\\'")).append("'],");
        sb.append("'speller':true");
        sb.append("}");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Api-Key " + yandexApiKey);
        headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);

        return new HttpEntity<>(sb.toString(), headers);
    }
}
