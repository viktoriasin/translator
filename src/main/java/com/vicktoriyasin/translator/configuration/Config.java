package com.vicktoriyasin.translator.configuration;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.vicktoriyasin.translator.engine.EngineType;
import com.vicktoriyasin.translator.engine.TranslationEngine;
import com.vicktoriyasin.translator.engine.TranslationEngineGoogle;
import com.vicktoriyasin.translator.engine.TranslationEngineYandex;
import com.vicktoriyasin.translator.jdbc.JdbcClient;
import com.vicktoriyasin.translator.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

@Configuration
@ComponentScan(basePackages = "com.vicktoriyasin.translator")
@PropertySource("classpath:/application.properties")
@EnableWebMvc
public class Config {

    @Value("${user_name}")
    private String userName;

    @Value("${user_password}")
    private String userPassword;

    @Value("${translation_engine_name}")
    private String translationEngineName;

    @Value("${translation_engine_yandex_api_key}")
    private String yandexApiKey;

    @Bean
    public DataSource dataSource() {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass("com.mysql.cj.jdbc.Driver");
        } catch (PropertyVetoException e) {
            Log.error(e);
        }

        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/translation_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        dataSource.setUser(userName);
        dataSource.setPassword(userPassword);

        return dataSource;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public JdbcClient jdbcClient() {
        return new JdbcClient();
    }

    @Bean
    public TranslationEngine translationEngine(@Autowired RestTemplate restTemplate) {
        if (translationEngineName.equalsIgnoreCase(EngineType.YANDEX.name)) {
            Log.info("Use Yandex Translation Engine");
            return new TranslationEngineYandex(yandexApiKey, restTemplate);
        } else {
            Log.info("Use Google Translation Engine");
            return new TranslationEngineGoogle(restTemplate);
        }
    }
}
