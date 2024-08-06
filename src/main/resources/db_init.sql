CREATE DATABASE IF NOT EXISTS translation_db;
USE translation_db;

CREATE TABLE IF NOT EXISTS translation_engines
(
    id   TINYINT     NOT NULL,
    name varchar(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS translation_visit_logs
(
    id                    BIGINT      NOT NULL AUTO_INCREMENT,
    visit_time            DATETIME    NOT NULL DEFAULT now(),
    ip                    varchar(50) NOT NULL,
    translation_engine_id TINYINT     NOT NULL,
    lang_from             varchar(50)          DEFAULT NULL,
    lang_to               varchar(50)          DEFAULT NULL,
    text_from             TEXT,
    text_to               TEXT,
    response_code         SMALLINT    NOT NULL,
    response_text         TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (translation_engine_id) REFERENCES translation_engines (id) ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS translation_word_cache
(
    translation_engine_id TINYINT      NOT NULL,
    lang_from             varchar(50)  NOT NULL,
    lang_to               varchar(50)  NOT NULL,
    word_from             varchar(100) NOT NULL,
    word_to               varchar(100) NOT NULL,
    words_sha2            BINARY(32) GENERATED ALWAYS AS (UNHEX(SHA2(CONCAT_WS('|', translation_engine_id, lang_from, lang_to, word_from), 256))),
    FOREIGN KEY (translation_engine_id) REFERENCES translation_engines (id) ON DELETE NO ACTION,
    UNIQUE INDEX (words_sha2)
);
