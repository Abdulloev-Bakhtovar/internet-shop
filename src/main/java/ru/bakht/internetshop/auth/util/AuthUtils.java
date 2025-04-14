package ru.bakht.internetshop.auth.util;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.service.LoginAttemptService;

@Component
public class AuthUtils {

    private static AuthenticationManager authenticationManager;
    private static LoginAttemptService loginAttemptService;

    public AuthUtils(AuthenticationManager authenticationManager, LoginAttemptService loginAttemptService) {
        AuthUtils.authenticationManager = authenticationManager;
        AuthUtils.loginAttemptService = loginAttemptService;
    }

    public User getAuthenticatedUser() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user;
        }

        throw new AppException("User is not authenticated", HttpStatus.UNAUTHORIZED);
    }

    public static void validateCredentials(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(email);
            throw new AppException("Invalid credentials for email: " + email, HttpStatus.UNAUTHORIZED);
        }
    }

    public static String getClientIp(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}
