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
import models.enums.UserType;
import models.json.pujari.PujariDetails;
import models.repos.CityRepository;
import models.repos.PujariRepository;
import models.sql.City;
import models.sql.Pujari;

import java.util.ArrayList;
import java.util.List;

@UserAnnotation
public enum AddPujariDetailsController implements BaseController {

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
        if (!request.getUser().getUserType().equals(UserType.PUJARI)) {
            throw new RoutingError("You are not permitted to access this data !");
        }
        Pujari pujari = PujariRepository.INSTANCE.exprFinder().eq("user.id" , request.getUser().getId()).findOne();
        if(pujari == null) {
            pujari = new Pujari();
            pujari.setUser(request.getUser());
            PujariDetails details = request.getRequest().get("details");
            pujari.setDetails(details);
            City city = CityRepository.INSTANCE.exprFinder().eq("id" , request.getRequest().get("id")).findOne();
            if(city == null) {
                throw new RoutingError("Invalid city Passed !");
            }
            pujari.setCity(city);
            pujari.save();
        } else {
            PujariDetails details = request.getRequest().get("details");
            pujari.setDetails(details);
            pujari.update();
        }
        return new SuccessResponse();
    }

    public List<RequestItem> items() {
        List<helpers.utils.RequestItem> items = new ArrayList<>();
        items.add(RequestItem.builder().key("cityId").itemType(RequestItemType.INTEGER).required(false).build());
        items.add(RequestItem.builder().key("details").itemType(RequestItemType.OBJECT).objectClass(PujariDetails.class).required(false).build());
        return items;
    }
}


