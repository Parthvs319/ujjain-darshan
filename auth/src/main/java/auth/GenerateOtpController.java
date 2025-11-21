package auth;

import helpers.blueprint.enums.RequestItemType;
import helpers.customErrors.RoutingError;
import helpers.interfaces.ParamsController;
import helpers.utils.RequestItem;
import helpers.utils.RequestZipped;
import helpers.utils.ResponseUtils;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.ext.web.RoutingContext;
import lombok.Data;
import models.repos.UserRepository;
import models.sql.User;
import rx.Single;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public enum GenerateOtpController implements ParamsController {

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

    private Response map(RequestZipped ctx) {
        Long userId = ctx.getRequest().get("userId");
        if (userId == null) {
            throw new RoutingError("userId missing");
        }
        User user = UserRepository.INSTANCE.byId(userId);
        if (user == null) {
            throw new RoutingError("Invalid user id !");
        }
        if(!user.isActive()) {
            throw new RoutingError("User is deactivated !");
        }
        String otp = UUID.randomUUID().toString();
        user.setCurrentOtp(otp);
        user.update();
        return new Response(otp);
    }

    @Override
    public List<RequestItem> items() {
        List<RequestItem> items = new ArrayList<>();
        items.add(
                RequestItem.builder()
                        .key("userId")
                        .required(true)
                        .itemType(RequestItemType.INTEGER)
                        .build()
        );
        return items;
    }

    @Data
    class Response {
        String otp;
        Response(String otp) {
            this.otp = otp;
        }
    }
}
