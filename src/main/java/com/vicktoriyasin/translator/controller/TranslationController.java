package com.vicktoriyasin.translator.controller;

import com.vicktoriyasin.translator.jdbc.JdbcClient;
import com.vicktoriyasin.translator.model.TranslationResult;
import com.vicktoriyasin.translator.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/", produces = "text/plain;charset=UTF-8")
public class TranslationController {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private JdbcClient jdbcClient;

    @GetMapping("/translation")
    public String translation(@RequestParam(required = false) String from, @RequestParam(required = false) String to, @RequestParam(required = false) String text, HttpServletRequest request, HttpServletResponse response) {
        TranslationResult translationResult = translationService.translate(from, to, text);

        String responseText = translationResult.text;
        int responseCode = translationResult.responseCode();

        if (translationResult.error != null) {
            responseText = translationResult.errorMessage();
        }

        jdbcClient.saveTranslationVisitLog(from, to, text, translationResult.text, request.getRemoteAddr(), responseCode, translationResult.errorMessage());

        response.setStatus(responseCode);
        return responseText;
    }
}
