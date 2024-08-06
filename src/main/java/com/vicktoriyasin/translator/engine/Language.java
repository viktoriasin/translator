package com.vicktoriyasin.translator.engine;

import java.util.HashMap;
import java.util.Map;

public enum Language {
    AUTO("auto", "Auto"),

    AFRIKAANS("af", "Afrikaans"),
    ALBANIAN("sq", "Albanian"),
    AMHARIC("am", "Amharic"),
    ARABIC("ar", "Arabic"),
    ARMENIAN("hy", "Armenian"),
    AZERBAIJANI("az", "Azerbaijani"),
    BASQUE("eu", "Basque"),
    BELARUSIAN("be", "Belarusian"),
    BENGALI("bn", "Bengali"),
    BOSNIAN("bs", "Bosnian"),
    BULGARIAN("bg", "Bulgarian"),
    CATALAN("ca", "Catalan"),
    CEBUANO("ceb", "Cebuano"),
    CHINESE("zh", "Chinese"),
    CROATIAN("hr", "Croatian"),
    CZECH("cs", "Czech"),
    DANISH("da", "Danish"),
    DUTCH("nl", "Dutch"),
    ENGLISH("en", "English"),
    ESPERANTO("eo", "Esperanto"),
    ESTONIAN("et", "Estonian"),
    FINNISH("fi", "Finnish"),
    FRENCH("fr", "French"),
    GALICIAN("gl", "Galician"),
    GEORGIAN("ka", "Georgian"),
    GERMAN("de", "German"),
    GREEK("el", "Greek"),
    GUJARATI("gu", "Gujarati"),
    HEBREW("he", "Hebrew"),
    HINDI("hi", "Hindi"),
    HUNGARIAN("hu", "Hungarian"),
    ICELANDIC("is", "Icelandic"),
    INDONESIAN("id", "Indonesian"),
    IRISH("ga", "Irish"),
    ITALIAN("it", "Italian"),
    JAPANESE("ja", "Japanese"),
    JAVANESE("jv", "Javanese"),
    KANNADA("kn", "Kannada"),
    KAZAKH("kk", "Kazakh"),
    KHMER("km", "Khmer"),
    KOREAN("ko", "Korean"),
    KYRGYZ("ky", "Kyrgyz"),
    LAO("lo", "Lao"),
    LATIN("la", "Latin"),
    LATVIAN("lv", "Latvian"),
    LITHUANIAN("lt", "Lithuanian"),
    LUXEMBOURGISH("lb", "Luxembourgish"),
    MACEDONIAN("mk", "Macedonian"),
    MALAGASY("mg", "Malagasy"),
    MALAY("ms", "Malay"),
    MALAYALAM("ml", "Malayalam"),
    MALTESE("mt", "Maltese"),
    MAORI("mi", "Maori"),
    MARATHI("mr", "Marathi"),
    MONGOLIAN("mn", "Mongolian"),
    NEPALI("ne", "Nepali"),
    NORWEGIAN("no", "Norwegian"),
    PERSIAN("fa", "Persian"),
    POLISH("pl", "Polish"),
    PORTUGUESE("pt", "Portuguese"),
    PUNJABI("pa", "Punjabi"),
    ROMANIAN("ro", "Romanian"),
    RUSSIAN("ru", "Russian"),
    SERBIAN("sr", "Serbian"),
    SINHALA("si", "Sinhala"),
    SLOVAK("sk", "Slovak"),
    SLOVENIAN("sl", "Slovenian"),
    SPANISH("es", "Spanish"),
    SUNDANESE("su", "Sundanese"),
    SWAHILI("sw", "Swahili"),
    SWEDISH("sv", "Swedish"),
    TAGALOG("tl", "Tagalog"),
    TAJIK("tg", "Tajik"),
    TAMIL("ta", "Tamil"),
    TATAR("tt", "Tatar"),
    TELUGU("te", "Telugu"),
    THAI("th", "Thai"),
    TURKISH("tr", "Turkish"),
    UKRAINIAN("uk", "Ukrainian"),
    URDU("ur", "Urdu"),
    UZBEK("uz", "Uzbek"),
    VIETNAMESE("vi", "Vietnamese"),
    WELSH("cy", "Welsh"),
    XHOSA("xh", "Xhosa"),
    YIDDISH("yi", "Yiddish"),
    ZULU("zu", "Zulu");

    public final String code;
    public final String rawLanguage;

    private static final Map<String, Language> codeToLang = new HashMap<>();
    private static final Map<String, Language> rawToLang = new HashMap<>();

    static {
        for (Language lang : Language.values()) {
            codeToLang.put(lang.code, lang);
            rawToLang.put(lang.rawLanguage.toLowerCase(), lang);
        }
    }

    Language(String code, String rawLanguage) {
        this.code = code;
        this.rawLanguage = rawLanguage;
    }

    public static Language parse(String raw) {
        Language lang = codeToLang.get(raw.toLowerCase());
        if (lang == null) {
            lang = rawToLang.get(raw.toLowerCase());
        }
        return lang;
    }
}
