package models.access.middlewear.user;



import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import helpers.customErrors.RoutingError;
import io.vertx.rxjava.ext.web.RoutingContext;
import models.access.tokens.TokenService;
import models.repos.UserRepository;
import models.sql.User;
import rx.Single;


public enum UserLoginMiddleware {
    INSTANCE;

    public Single<RoutingContext> authenticationObservable(RoutingContext rc) {
        return Single.just(rc)
                .map(this::authenticateRequest);
    }

    private RoutingContext authenticateRequest(RoutingContext rc) {
        String header = rc.request().getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer "))
            throw new RoutingError(401, "Missing or invalid Authorization header");

        String token = header.substring(7);
        DecodedJWT jwt = TokenService.decodedJWT(token);

        Claim type = jwt.getClaim("type");
        if (type == null || !"access_token".equals(type.asString()))
            throw new RoutingError(401, "Invalid token type");

        Long userId = jwt.getClaim("id").asLong();
        if (userId == null)
            throw new RoutingError(401, "Invalid user token");

        User user = UserRepository.INSTANCE.byId(userId);
        if (user == null || user.isDeleted())
            throw new RoutingError(401, "User not found");

        rc.put("user", user);
        return rc;
    }
}