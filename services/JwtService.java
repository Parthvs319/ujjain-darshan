package src.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtService {
    private static final String SECRET = System.getenv().getOrDefault("JWT_SECRET", "change-this-secret-to-a-very-long-random-value");
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final long EXP_MS = 1000L * 60 * 60 * 24 * 7; // 7 days

    public static String issueToken(Long userId, String userType, String tenantId) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXP_MS))
                .addClaims(Map.of("userType", userType, "tenantId", tenantId))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Jws<Claims> parseToken(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public static Long subjectAsUserId(Jws<Claims> jwt) {
        String sub = jwt.getBody().getSubject();
        return sub == null ? null : Long.parseLong(sub);
    }
}
