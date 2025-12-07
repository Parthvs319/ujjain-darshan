package pujari;

import helpers.annotations.UserAnnotation;
import helpers.blueprint.enums.RequestItemType;
import helpers.customErrors.RoutingError;
import helpers.interfaces.BaseController;
import helpers.utils.RequestItem;
import helpers.utils.ResponseUtils;
import helpers.utils.SuccessResponse;
import io.vertx.rxjava.ext.web.RoutingContext;
import models.access.middlewear.user.UserAccessMiddleware;
import models.body.UserLoginRequest;
import models.enums.Status;
import models.enums.UserType;
import models.json.pujari.PujariDetails;
import models.repos.PujariRepository;
import models.sql.Pujari;

import java.util.ArrayList;
import java.util.List;

@UserAnnotation
public enum VerifyPujariDetailsController implements BaseController {

    INSTANCE;

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
        if (!request.getUser().getUserType().equals(UserType.ADMIN)) {
            throw new RoutingError("You are not permitted to access this data !");
        }
        Pujari pujari = PujariRepository.INSTANCE.exprFinder().eq("id" , request.getRequest().get("id")).findOne();
        if(pujari == null) {
            throw new RoutingError("Pujari with this id not found !");
        } else {
            PujariDetails details = pujari.getDetails();
            details.setVerified(request.getRequest().get("verified"));
            pujari.setDetails(details);
            pujari.setStatus(Status.APPROVED);
            pujari.update();
        }
        return new SuccessResponse();
    }

    public List<RequestItem> items() {
        List<helpers.utils.RequestItem> items = new ArrayList<>();
        items.add(RequestItem.builder().key("id").itemType(RequestItemType.INTEGER).required(false).build());
        items.add(RequestItem.builder().key("verified").itemType(RequestItemType.BOOLEAN).objectClass(PujariDetails.class).required(false).build());
        return items;
    }
}



