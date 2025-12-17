package driver;

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
import models.repos.VehiclesRepository;
import models.sql.Vehicles;

import java.util.ArrayList;
import java.util.List;

@UserAnnotation
public enum UpdateVehicleStatusController implements BaseController {

    INSTANCE;

    @Override
    public void handle(RoutingContext event) {
        UserAccessMiddleware.INSTANCE.with(event, new ArrayList<>(), this.getClass())
                .map(this::map)
                .subscribe(
                        o -> ResponseUtils.INSTANCE.writeJsonResponse(event, o),
                        error -> ResponseUtils.INSTANCE.handleError(event, error)
                );
    }

    private SuccessResponse map(UserLoginRequest request) {
        try {
            if(!request.getUser().getUserType().equals(UserType.ADMIN)) {
                throw new RoutingError("You can not approve a driver!");
            }
            Vehicles vehicles = VehiclesRepository.INSTANCE.exprFinder().eq("id" , request.getRequest().get("id")).findOne();
            if(vehicles == null) {
                throw new RoutingError("Invalid driver Id passed !");
            } else {
                Status status = Status.valueOf(request.getRequest().get("status"));
                vehicles.setStatus(status);
                vehicles.update();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RoutingError(e.getMessage());
        }
        return new SuccessResponse();
    }

    public List<RequestItem> items() {
        List<helpers.utils.RequestItem> items = new ArrayList<>();
        items.add(RequestItem.builder().required(true).itemType(RequestItemType.INTEGER).key("id").build());
        items.add(RequestItem.builder().required(true).itemType(RequestItemType.STRING).key("status").build());
        return items;
    }

}




