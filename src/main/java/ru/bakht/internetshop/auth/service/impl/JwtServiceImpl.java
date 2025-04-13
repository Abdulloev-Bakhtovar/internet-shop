package ru.bakht.internetshop.auth.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import ru.bakht.internetshop.auth.exception.KvadroksException;
import ru.bakht.internetshop.auth.model.TokenClaims;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.service.JwtService;
import ru.bakht.internetshop.auth.util.KeyProviderUtil;

import java.nio.file.NoSuchFileException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${application.security.jwt.access-token.expiration}")
    private int accessTokenExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private int refreshTokenExpiration;

    @Value("${application.security.jwt.keys.public}")
    private String publicKeyFromFile;

    @Value("${application.security.jwt.keys.private}")
    private String privateKeyFromFile;

    private static final String TOKEN_TYPE_ACCESS = "ACCESS";
    private static final String TOKEN_TYPE_REFRESH = "REFRESH";

    @Override
    public String extractEmail(String jwt) {
        return extractClaim(jwt, claims -> claims.get("email", String.class));
    }

    @Override
    public String extractUserId(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    @Override
    public String extractTokenType(String jwt) {
        return extractClaim(jwt, claims -> claims.get("tokenType", String.class));
    }

    @Override
    public Instant extractExpiration(String token) {
        return extractClaim(token, claims ->
                claims.getExpiration().toInstant());
    }

    @Override
    public Collection<? extends GrantedAuthority> extractAuthorities(String jwt) {
        List<?> roles = extractClaim(jwt, claims -> claims.get("roles", List.class));

        if (roles == null || roles.isEmpty()) {
            throw new KvadroksException("JWT token is malformed", HttpStatus.BAD_REQUEST);
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .toList();
    }

    @Override
    public String generateAccessToken(User user) {
        TokenClaims claims = new TokenClaims(
                user.getEmail(),
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList(),
                TOKEN_TYPE_ACCESS
        );
        return generateToken(user, accessTokenExpiration, claims.toMap());
    }

    @Override
    public String generateRefreshToken(User user) {
        TokenClaims claims = new TokenClaims(
                user.getEmail(),
                null,
                TOKEN_TYPE_REFRESH
        );
        return generateToken(user, refreshTokenExpiration, claims.toMap());
    }

    private String generateToken(User user,
                                 long expirationMinutes,
                                 Map<String, Object> customClaims) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiration = Date.from(now.plus(Duration.ofMinutes(expirationMinutes)));

        try {
            PrivateKey privateKey = KeyProviderUtil.getPrivateKey(this.privateKeyFromFile);

            return Jwts.builder()
                    .subject(String.valueOf(user.getId()))
                    .claims(customClaims)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .signWith(privateKey)
                    .compact();
        } catch (NoSuchFileException ex) {
            throw new KvadroksException("Private key file not found. Please check the file path.",
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new KvadroksException("Error while signing JWT",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean isTokenExpired(String token) {
        boolean isExpired = extractExpiration(token).isBefore(Instant.now());
        if (isExpired) {
            throw new KvadroksException("JWT token has expired", HttpStatus.UNAUTHORIZED);
        }
        return isExpired;
    }

    @Override
    public boolean isTokenSignatureValid(String token) {
        try {
            PublicKey publicKey = KeyProviderUtil.getPublicKey(this.publicKeyFromFile);

            Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isRefreshToken(String token) {
        return TOKEN_TYPE_REFRESH.equals(extractTokenType(token));
    }

    @Override
    public boolean isAccessToken(String token) {
        return TOKEN_TYPE_ACCESS.equals(extractTokenType(token));
    }

    private Claims extractAllClaims(String token) {
        try {
            PublicKey publicKey = KeyProviderUtil.getPublicKey(this.publicKeyFromFile);

            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new KvadroksException("JWT token has expired", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new KvadroksException("JWT token is malformed", HttpStatus.UNAUTHORIZED);
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
}
