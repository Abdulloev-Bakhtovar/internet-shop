package ru.bakht.internetshop.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${application.email.host}")
    private String host;

    @Value("${application.email.port}")
    private int port;

    @Value("${application.email.username}")
    private String username;

    @Value("${application.email.password}")
    private String password;

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Настройте SMTP-сервер и порт
        mailSender.setHost(host);
        mailSender.setPort(port);

        // Настройка авторизации
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        // Настройка свойств JavaMail для SSL
        Properties properties = getProperties();

        mailSender.setJavaMailProperties(properties);

        return mailSender;
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.trust", host);
        properties.put("mail.smtp.socketFactory.class", "jakarta.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.port", port);
        return properties;
    }
}
