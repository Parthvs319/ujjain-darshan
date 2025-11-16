package models.access.tokens;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.util.Date;

public enum TokenService {

    INSTANCE;

    private static final String SECRET = "D9dka98s-1XnPwLq3uT2z8-3YtKp0sVd";
    private static final String ISSUER = "my-app";

    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    public static BearerToken generateToken(Long userId, String email , String userType , String userName) {
        String token =  JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600_000)) // expires in 1 hour
                .withClaim("id", userId)
                .withClaim("userId", userId)
                .withClaim("email", email)
                .withClaim("type", "access_token")
                .withClaim("userType", userType)
                .sign(algorithm);

        return BearerToken.builder().accessToken(token)
                .name(userName)
                .username(email)
                .expiresAt(new Date(System.currentTimeMillis() + 3600_000).getTime())
                .tokenType("Bearer")
                .build();
    }

    public static DecodedJWT decodedJWT(String token) {
        return JWT.decode(token);
    }

    public static DecodedJWT verifyToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
        return verifier.verify(token);
    }

    // 🔸 Decode (optional)
    public static void printDecodedClaims(String token) {
        DecodedJWT jwt = JWT.decode(token);
        System.out.println("User ID: " + jwt.getClaim("userId").asLong());
        System.out.println("Email: " + jwt.getClaim("email").asString());
        System.out.println("Expires At: " + jwt.getExpiresAt());
    }
}