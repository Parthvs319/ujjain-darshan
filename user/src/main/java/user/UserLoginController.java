package user;

import helpers.blueprint.enums.RequestItemType;
import helpers.interfaces.BaseController;
import helpers.utils.RequestItem;
import helpers.utils.RequestZipped;
import helpers.utils.ResponseUtils;
import helpers.utils.SuccessResponse;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.ext.web.RoutingContext;
import models.access.middlewear.UserAccessMiddleware;
import models.body.UserLoginRequest;
import rx.Single;

import java.util.ArrayList;
import java.util.List;

public enum UserLoginController implements BaseController {

    INSTANCE;

    public List<RequestItem> items() {
        List<helpers.utils.RequestItem> items = new ArrayList<>();
        items.add(helpers.utils.RequestItem.builder().key("mobile").itemType(RequestItemType.STRING).required(true).build());
        items.add(helpers.utils.RequestItem.builder().key("password").itemType(RequestItemType.STRING).required(true).build());
        items.add(helpers.utils.RequestItem.builder().key("name").itemType(RequestItemType.STRING).required(true).build());
        items.add(helpers.utils.RequestItem.builder().key("email").itemType(RequestItemType.STRING).required(true).build());
        items.add(helpers.utils.RequestItem.builder().key("userType").itemType(RequestItemType.STRING).required(true).build());
        items.add(helpers.utils.RequestItem.builder().key("residingCity").itemType(RequestItemType.STRING).required(false).build());
        return items;
    }

    @Override
    public void handle(RoutingContext event) {

        UserAccessMiddleware.INSTANCE.with(event , items() ,  this.getClass())
                .map(this::map)
                .subscribe(
                        o -> ResponseUtils.INSTANCE.writeJsonResponse(event, o),
                        error -> {
                            ResponseUtils.INSTANCE.handleError(event, error);
                        }
                );
    }

    private SuccessResponse map(UserLoginRequest request) {



        return new SuccessResponse();
    }

}
