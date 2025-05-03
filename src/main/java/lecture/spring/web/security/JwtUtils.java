package lecture.spring.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Date;

public class JwtUtils {
    private static final SecretKey secretKey = generateKey();

    public static String generateToken(String subject, String scope) {
        return Jwts.builder()
                .subject(subject)
                .claim("scope", scope)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2)) // 2 hours
                .signWith(secretKey)
                .compact();
    }

    public static Claims verifyToken(String token) {
        Claims claims = extractClaims(token);
        if (claims.getExpiration().before(new Date())) {
            return null;
        }
        return claims;
    }

    private static Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private static SecretKey generateKey() {
        byte[] keyBytes = new byte[32]; // 256 bits
        new SecureRandom().nextBytes(keyBytes);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
