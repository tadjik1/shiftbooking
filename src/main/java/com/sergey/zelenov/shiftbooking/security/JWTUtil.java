package com.sergey.zelenov.shiftbooking.security;

import com.sergey.zelenov.shiftbooking.exception.InvalidJWTSignature;
import com.sergey.zelenov.shiftbooking.model.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {

    private final SecretKey key = Jwts.SIG.HS256.key().build();

    @Value("${app.jwtExpirationMs}")
    private long jwtExpirationMs;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

        return Jwts.builder()
                .subject(user.getUsername())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() +jwtExpirationMs))
                .signWith(key)
                .compact();
    }
    public Map<String, Object> extractUserDetails(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("username", username);
            userDetails.put("role", role);

            return userDetails;
        } catch (JwtException e) {
            throw new InvalidJWTSignature("token is invalid");
        }
    }
}
