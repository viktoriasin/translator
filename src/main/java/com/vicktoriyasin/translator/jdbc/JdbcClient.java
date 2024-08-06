package com.vicktoriyasin.translator.jdbc;

import com.vicktoriyasin.translator.engine.TranslationEngine;
import com.vicktoriyasin.translator.model.TranslationRequest;
import com.vicktoriyasin.translator.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class JdbcClient {

    @Autowired
    DataSource dataSource;

    @Autowired
    private TranslationEngine translationEngine;

    public void saveTranslationVisitLog(String langFrom, String langTo, String textFrom, String textTo, String ip, int responseCode, String errorMessage) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO translation_visit_logs("
                + "ip, translation_engine_id, lang_from, lang_to, text_from, text_to, response_code, response_text)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, ip);
            preparedStatement.setInt(2, translationEngine.engineType().id);
            preparedStatement.setString(3, langFrom);
            preparedStatement.setString(4, langTo);
            preparedStatement.setString(5, textFrom);
            preparedStatement.setString(6, textTo);
            preparedStatement.setInt(7, responseCode);
            preparedStatement.setString(8, errorMessage);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                Log.error("Failed to add record to database");
            }
        } catch (SQLException e) {
            Log.error(e);
        } finally {
            closeResources(connection, preparedStatement);
        }
    }

    public void saveTranslatedWord(TranslationRequest request, String translated) {
        saveTranslatedWord(request.engineType.id, request.from.code, request.to.code, request.text, translated);
    }

    public void saveTranslatedWord(Integer translationEngineId, String langFrom, String langTo, String wordFrom, String wordTo) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO translation_word_cache("
                + "translation_engine_id, lang_from, lang_to, word_from, word_to)"
                + " VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, translationEngineId);
            preparedStatement.setString(2, langFrom);
            preparedStatement.setString(3, langTo);
            preparedStatement.setString(4, wordFrom);
            preparedStatement.setString(5, wordTo);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                Log.error("Failed to add record to database");
            }

            preparedStatement.close();
        } catch (SQLException ignored) {
        } finally {
            closeResources(connection, preparedStatement);
        }
    }

    public String getTranslatedWord(TranslationRequest request) {
        return getTranslatedWord(request.engineType.id, request.from.code, request.to.code, request.text);
    }

    public String getTranslatedWord(Integer translationEngineId, String langFrom, String langTo, String wordFrom) {
        Connection connection = null;
        String translatedWord = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("SELECT word_to FROM translation_word_cache " +
                "WHERE words_sha2 = UNHEX(SHA2(CONCAT_WS('|', ?, ?, ?, ?),256)) " +
                "and translation_engine_id = ? " + // To avoid collisions
                "and lang_from = ? " +
                "and lang_to = ? " +
                "and word_from = ?"
            );
            preparedStatement.setInt(1, translationEngineId);
            preparedStatement.setString(2, langFrom);
            preparedStatement.setString(3, langTo);
            preparedStatement.setString(4, wordFrom);
            preparedStatement.setInt(5, translationEngineId);
            preparedStatement.setString(6, langFrom);
            preparedStatement.setString(7, langTo);
            preparedStatement.setString(8, wordFrom);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            translatedWord = resultSet.getString("word_to");
        } catch (SQLException ignored) {
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    Log.error(e);
                }
            }
            closeResources(connection, preparedStatement);
        }
        return translatedWord;
    }

    private void closeResources(Connection connection, PreparedStatement preparedStatement) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                Log.error(e);
            }
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                Log.error(e);
            }
        }
    }
}
