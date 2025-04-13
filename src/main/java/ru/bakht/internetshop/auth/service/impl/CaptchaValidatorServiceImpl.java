package ru.bakht.internetshop.auth.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.bakht.internetshop.auth.service.CaptchaValidatorService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CaptchaValidatorServiceImpl implements CaptchaValidatorService {

    @Value("${google.recaptcha.secret-key}")
    private String secretKey;

    @Value("${google.recaptcha.threshold}")
    private float threshold;

    @Value("${google.recaptcha.verify-url}")
    private String verifyUrl;

    @Value("${google.recaptcha.test-mode}")
    private boolean testMode;

    private final RestTemplate restTemplate;

    @Override
    public boolean validate(String captchaResponse, String clientIp) {

        if (testMode && captchaResponse.equals("test")) {
            return true;
        }

        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", secretKey);
            params.add("response", captchaResponse);
            params.add("remoteip", clientIp);

            ResponseEntity<RecaptchaResponse> response = restTemplate.postForEntity(
                    verifyUrl,
                    params,
                    RecaptchaResponse.class
            );

            return response.getStatusCode() == HttpStatus.OK &&
                    response.getBody() != null &&
                    response.getBody().isSuccess() &&
                    response.getBody().getScore() >= threshold;
        } catch (Exception e) {
            return false;
        }
    }

    @Data
    private static class RecaptchaResponse {
        private boolean success;
        private float score;
        private String action;
        @JsonProperty("challenge_ts")
        private String challengeTs;
        private String hostname;
        private List<String> errorCodes;
    }
}