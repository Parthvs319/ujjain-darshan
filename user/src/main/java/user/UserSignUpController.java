package user;

import helpers.annotations.UserAnnotation;
import helpers.interfaces.BaseController;
import helpers.utils.ResponseUtils;
import helpers.utils.SuccessResponse;
import io.vertx.rxjava.ext.web.RoutingContext;
import models.access.middlewear.UserAccessMiddleware;
import models.body.UserLoginRequest;

import java.util.ArrayList;

@UserAnnotation
public enum UserSignUpController implements BaseController {

    INSTANCE;

    @Override
    public void handle(RoutingContext event) {
       UserAccessMiddleware.INSTANCE.with(event, new ArrayList<>(), this)
                .map(this::map)
                .subscribe(
                        response -> ResponseUtils.INSTANCE.writeJsonResponse(event, response),
                        error -> ResponseUtils.INSTANCE.handleError(event, error)
                );
    }

    private SuccessResponse map (UserLoginRequest context) {
        return new SuccessResponse();
    }
}
