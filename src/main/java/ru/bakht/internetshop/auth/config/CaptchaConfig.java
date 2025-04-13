package ru.bakht.internetshop.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CaptchaConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}