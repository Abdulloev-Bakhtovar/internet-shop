package ru.bakht.internetshop.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.security")
public class SecurityProperties {

    private List<String> corsAllowedOrigins = List.of();
    private List<String> publicEndpoints = List.of();
    private List<String> excludedPaths = List.of();
}