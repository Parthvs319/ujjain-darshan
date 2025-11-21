package models.access.tokens;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import helpers.customErrors.RoutingError;
import helpers.utils.PasswordUtils;
import models.repos.UserRepository;
import models.sql.RefreshToken;
import models.sql.User;

import java.util.Date;

public enum TokenService {

    INSTANCE;

    private static final String SECRET = "D9dka98s-1XnPwLq3uT2z8-3YtKp0sVd";
    private static final String ISSUER = "my-app";
    private static final long accessExpiry = System.currentTimeMillis() + 3600_000;        // 1 hour
    private static final long refreshExpiry = System.currentTimeMillis() + 30L * 24 * 3600_000; // 30 days
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    public synchronized static BearerToken generateToken(Long userId, String email, String userType, String userName) {

        String accessToken = JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(accessExpiry))
                .withClaim("id", userId)
                .withClaim("email", email)
                .withClaim("type", "access_token")
                .withClaim("userType", userType)
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(refreshExpiry))
                .withClaim("id", userId)
                .withClaim("email", email)
                .withClaim("type", "refresh_token")
                .sign(algorithm);

        RefreshToken old = RefreshToken.byUserId(userId);
        if (old != null) {
            old.setRevoked(true);
            old.update();
        }

        RefreshToken ref = new RefreshToken();
        User user = UserRepository.INSTANCE.byId(userId);
        if(user == null) {
            throw new RoutingError("User not found !");
        }
        ref.setUser(user);
        ref.setTokenHash(refreshToken);
        ref.setExpiresAt(new Date(refreshExpiry));
        ref.save();

        return BearerToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .name(userName)
                .username(email)
                .expiresAt(accessExpiry)
                .tokenType("Bearer")
                .build();
    }

    public static BearerToken generateAccessToken(Long userId, String email, String userType, String userName , String refreshToken) {
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(accessExpiry))
                .withClaim("id", userId)
                .withClaim("userId", userId)
                .withClaim("email", email)
                .withClaim("type", "access_token")
                .withClaim("userType", userType)
                .sign(algorithm);

        return BearerToken.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .name(userName)
                .username(email)
                .expiresAt(accessExpiry)
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