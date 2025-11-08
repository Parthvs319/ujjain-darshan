package src.web.handlers;

import io.vertx.ext.web.RoutingContext;
import src.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import src.web.exceptions.UnauthorizedException;

/**
 * Middleware to protect routes. Put the user info in routingContext with keys: userId, userType, tenantId
 */
public class AuthMiddleware {
    public static void requireAuth(RoutingContext ctx) {
        String auth = ctx.request().getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            ctx.fail(new UnauthorizedException("Missing Authorization header"));
            return;
        }
        String token = auth.substring("Bearer ".length());
        try {
            Jws<Claims> jwt = JwtService.parseToken(token);
            Long userId = JwtService.subjectAsUserId(jwt);
            String userType = (String) jwt.getBody().get("userType");
            String tenantId = (String) jwt.getBody().get("tenantId");
            ctx.put("userId", userId);
            ctx.put("userType", userType);
            ctx.put("tenantId", tenantId);
            ctx.next();
        } catch (Exception e) {
            ctx.fail(new UnauthorizedException("Invalid token"));
        }
    }
}
