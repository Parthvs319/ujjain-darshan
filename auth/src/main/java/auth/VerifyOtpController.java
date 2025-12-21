package auth;

import helpers.blueprint.enums.RequestItemType;
import helpers.interfaces.ParamsController;
import models.services.OtpService;
import helpers.utils.RequestItem;
import helpers.utils.RequestZipped;
import helpers.utils.ResponseUtils;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.ext.web.RoutingContext;
import lombok.Data;
import rx.Single;

import java.util.ArrayList;
import java.util.List;


public enum VerifyOtpController  implements ParamsController {

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
        return new Response(OtpService.INSTANCE.verifyOtp(ctx.getRequest().get("mobile") , ctx.getRequest().get("otp")));
    }

    @Override
    public List<RequestItem> items() {
        List<RequestItem> items = new ArrayList<>();
        items.add(
                RequestItem.builder()
                        .key("mobile")
                        .required(true)
                        .itemType(RequestItemType.STRING)
                        .build()
        );

        items.add(
                RequestItem.builder()
                        .key("otp")
                        .required(true)
                        .itemType(RequestItemType.STRING)
                        .build()
        );
        return items;
    }

    @Data
    class Response {
        Boolean verified;
        Response(Boolean verified) {
            this.verified = verified;
        }
    }
}

