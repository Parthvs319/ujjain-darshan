package user;

import helpers.annotations.UserAnnotation;
import helpers.interfaces.BaseController;
import helpers.middlewear.UserAccessMiddleware;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;

@UserAnnotation
public enum CreateUserController {

    INSTANCE;


//    public void handle(RoutingContext event) {
//        UserAccessMiddleware.INSTANCE.with(event, new ArrayList<>(), this)
//                .map(this::map)
//                .subscribe(
//                        response -> ResponseHelper.INSTANCE.writeJsonResponse(event, response),
//                        error -> ResponseHelper.INSTANCE.handleError(event, error)
//                );
//    }




}
