package driver;

import helpers.annotations.UserAnnotation;
import helpers.customErrors.RoutingError;
import helpers.interfaces.BaseController;
import helpers.utils.RequestItem;
import helpers.utils.ResponseUtils;
import helpers.utils.SuccessResponse;
import io.vertx.rxjava.ext.web.RoutingContext;
import models.access.middlewear.user.UserAccessMiddleware;
import models.body.UserLoginRequest;
import models.enums.UserType;
import models.repos.VehiclesRepository;
import models.sql.Vehicles;

import java.util.ArrayList;
import java.util.List;

@UserAnnotation
public enum RemoveVehicleController implements BaseController {

    INSTANCE;

    @Override
    public void handle(RoutingContext event) {
        UserAccessMiddleware.INSTANCE.with(event, items(), this.getClass())
                .map(this::map)
                .subscribe(
                        o -> ResponseUtils.INSTANCE.writeJsonResponse(event, o),
                        error -> ResponseUtils.INSTANCE.handleError(event, error)
                );
    }

    private SuccessResponse map(UserLoginRequest request) {
        Long vehicleId = request.getRequest().get("id");
        if (vehicleId == null) {
            throw new RoutingError("Invalid vehicle id");
        }

        Vehicles vehicle = VehiclesRepository.INSTANCE.exprFinder().eq("id", vehicleId).findOne();
        if (vehicle == null) {
            throw new RoutingError("Vehicle not found");
        }

        boolean isAdmin = request.getUser().getUserType() == UserType.ADMIN;
        boolean isOwner = vehicle.getUser() != null && vehicle.getUser().getId().equals(request.getUser().getId());
        if (!isAdmin && !isOwner) {
            throw new RoutingError("You are not permitted to delete this vehicle");
        }

        vehicle.setDeleted(true);
        vehicle.update();
        return new SuccessResponse(true, "Vehicle removed successfully");
    }

    public List<RequestItem> items() {
        List<RequestItem> items = new ArrayList<>();
        items.add(RequestItem.builder().key("id").required(true).itemType(helpers.blueprint.enums.RequestItemType.INTEGER).build());
        return items;
    }
}


