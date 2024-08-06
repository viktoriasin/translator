package com.vicktoriyasin.translator.engine;

import com.google.gson.JsonParser;
import com.vicktoriyasin.translator.model.TranslationResult;
import com.vicktoriyasin.translator.util.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.vicktoriyasin.translator.model.TranslationError.ENGINE_ERROR;
import static com.vicktoriyasin.translator.model.TranslationError.NETWORK_ERROR;

public class TranslationEngineGoogle implements TranslationEngine {

    private final RestTemplate restTemplate;

    public TranslationEngineGoogle(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public EngineType engineType() {
        return EngineType.GOOGLE;
    }

    public TranslationResult translate(Language from, Language to, String text) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(createUri(from, to, text), String.class);

            String body = response.getBody();
            if (body == null) {
                Log.error("Engine error in TranslationEngineGoogle for '" + text + "'");
                return new TranslationResult(ENGINE_ERROR);
            }

            String translatedText = JsonParser.parseString(body)
                .getAsJsonArray().get(0)
                .getAsJsonArray().get(0)
                .getAsJsonArray().get(0)
                .getAsString();

            return new TranslationResult(translatedText);

        } catch (ResourceAccessException e) {
            Log.error("Network error in TranslationEngineGoogle for '" + text + "'");
            return new TranslationResult(NETWORK_ERROR);
        } catch (Exception e) {
            Log.error("Engine error in TranslationEngineGoogle for '" + text + "'");
            return new TranslationResult(ENGINE_ERROR);
        }
    }

    private URI createUri(Language from, Language to, String text) {
        String uriString = UriComponentsBuilder.fromUriString("https://translate.googleapis.com/translate_a/single")
            .queryParam("dt", "at", "bd", "ex", "ld", "md", "qca", "rw", "rm", "ss", "t")
            .queryParam("client", "gtx")
            .queryParam("ie", "UTF-8")
            .queryParam("oe", "UTF-8")
            .queryParam("otf", 1)
            .queryParam("ssel", 0)
            .queryParam("tsel", 0)
            .queryParam("sl", from.code)
            .queryParam("tl", to.code)
            .queryParam("hl", to.code)
            .queryParam("q", text)
            .toUriString();

        return URI.create(uriString);
    }
}
