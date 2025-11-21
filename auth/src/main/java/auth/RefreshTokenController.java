package auth;

import helpers.blueprint.enums.RequestItemType;
import helpers.customErrors.RoutingError;
import helpers.interfaces.ParamsController;
import helpers.utils.*;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.ext.web.RoutingContext;
import models.access.tokens.BearerToken;
import models.access.tokens.TokenService;
import models.sql.User;
import rx.Single;

import java.util.ArrayList;
import java.util.List;

public enum RefreshTokenController implements ParamsController {

    INSTANCE;

    @Override
    public void handle(RoutingContext event) {
        Single.just(event)
                .subscribeOn(RxHelper.blockingScheduler(event.vertx()))
                .map(this::map)
                .map(this::map)
                .subscribe(
                        o -> ResponseUtils.INSTANCE.writeJsonResponse(event, o),
                        error -> ResponseUtils.INSTANCE.handleError(event, error)
                );
    }

    private BearerToken map(RequestZipped ctx) {
        String refreshToken = ctx.getRequest().get("refreshToken");
        if (refreshToken == null) {
            throw new RoutingError("refreshToken missing");
        }
        String hashed = PasswordUtils.INSTANCE.hash(refreshToken);
        models.sql.RefreshToken stored = models.sql.RefreshToken.byToken(hashed);
        if (stored == null || stored.getRevoked()) {
            throw new RoutingError("Invalid refresh token");
        }
        //// This is to be done later using cron / automation queue !
        if (stored.getExpiresAt().before(new java.util.Date())) {
            stored.setRevoked(true);
            stored.update();
            throw new RoutingError("Refresh token expired");
        }
        User user = stored.getUser();
        if (user == null) {
            throw new RoutingError("User not found");
        }

        return TokenService.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getUserType().name(),
                user.getName(),
                refreshToken
        );
    }

    @Override
    public List<RequestItem> items() {
        List<RequestItem> items = new ArrayList<>();
        items.add(
                RequestItem.builder()
                        .key("refreshToken")
                        .required(true)
                        .itemType(RequestItemType.STRING)
                        .build()
        );
        return items;
    }
}