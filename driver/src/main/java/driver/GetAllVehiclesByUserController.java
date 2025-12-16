package driver;

import helpers.annotations.UserAnnotation;
import helpers.customErrors.RoutingError;
import helpers.interfaces.BaseController;
import helpers.utils.RequestItem;
import helpers.utils.ResponseUtils;
import io.vertx.rxjava.ext.web.RoutingContext;
import lombok.Data;
import models.access.middlewear.user.UserAccessMiddleware;
import models.body.UserLoginRequest;
import models.json.vehicles.VehicleDetails;
import models.repos.VehiclesRepository;
import models.sql.Vehicles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UserAnnotation
public enum GetAllVehiclesByUserController implements BaseController {

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

    private Response map(UserLoginRequest request) {
        Long userId = request.getRequest().get("id");
        if (userId == null) {
            throw new RoutingError("Invalid user id");
        }
        // Allow admins or the same user to view their vehicles
        if (!request.getUser().getId().equals(userId) && request.getUser().getUserType() != models.enums.UserType.ADMIN) {
            throw new RoutingError("You are not permitted to access this data !");
        }

        List<Vehicles> vehicles = VehiclesRepository.INSTANCE.exprFinder()
                .eq("user.id", userId)
                .findList();

        Response response = new Response();
        response.setResponse(vehicles);
        return response;
    }

    public List<RequestItem> items() {
        List<RequestItem> items = new ArrayList<>();
        items.add(RequestItem.builder().key("id").required(true).itemType(helpers.blueprint.enums.RequestItemType.INTEGER).build());
        return items;
    }

    @Data
    class Response {
        private List<VehicleDTO> vehicles = new ArrayList<>();

        private void setResponse(List<models.sql.Vehicles> dbVehicles) {
            this.vehicles = dbVehicles.stream()
                    .map(VehicleDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    class VehicleDTO {
        private Long id;
        private String number;
        private String status;
        private Long userId;
        private Long driverId;
        private String city;
        private VehicleDetails details;

        VehicleDTO(models.sql.Vehicles vehicle) {
            this.id = vehicle.getId();
            this.number = vehicle.getNumber();
            this.status = vehicle.getStatus().name();
            this.userId = vehicle.getUser() != null ? vehicle.getUser().getId() : null;
            this.driverId = vehicle.getDriver() != null ? vehicle.getDriver().getId() : null;
            this.city = vehicle.getCity() != null ? vehicle.getCity().getName() : null;
            this.details = vehicle.getDetails();
        }
    }
}


