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
import models.json.vehicles.VehicleDetails;
import models.repos.CityRepository;
import models.repos.DriverRepository;
import models.repos.VehiclesRepository;
import models.sql.City;
import models.sql.Drivers;
import models.sql.Vehicles;
import java.util.ArrayList;
import java.util.List;

@UserAnnotation
public enum AddVehicleController implements BaseController {

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
            if(!request.getUser().getUserType().equals(UserType.DRIVER)) {
                throw new RoutingError("You can not add vehicles as you are not a driver !");
            }
            String number =  request.getRequest().get("vehicleNumber");
            if(VehiclesRepository.INSTANCE.exprFinder().eq("number" , number).findOne() != null) {
                throw new RoutingError("Vehicle with the same vehicle already registered !");
            } else {
                Vehicles vehicles = new Vehicles();
                Drivers driver = DriverRepository.INSTANCE.exprFinder().eq("id" , request.getRequest().get("driverId")).findOne();
                if(driver == null) {
                    throw new RoutingError("Invalid driver Id passed !");
                } else {
                    City city = CityRepository.INSTANCE.exprFinder().eq("id" , request.getRequest().get("cityId")).findOne();
                    if(city == null) {
                        throw new RoutingError("invalid City id passed !");
                    } else {
                        vehicles.setUser(request.getUser());
                        vehicles.setDriver(driver);
                        vehicles.setCity(city);
                        vehicles.setNumber(number);
                        vehicles.setStatus(Status.PENDING);
                        vehicles.setDetails(request.getRequest().get("details"));
                        vehicles.save();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RoutingError(e.getMessage());
        }
        return new SuccessResponse();
    }

    public List<RequestItem> items() {
        List<helpers.utils.RequestItem> items = new ArrayList<>();
        items.add(RequestItem.builder().key("details").required(true).itemType(RequestItemType.OBJECT).objectClass(VehicleDetails.class).build());
        items.add(RequestItem.builder().required(true).itemType(RequestItemType.INTEGER).key("driverId").build());
        items.add(RequestItem.builder().required(true).itemType(RequestItemType.STRING).key("vehicleNumber").build());
        items.add(RequestItem.builder().required(true).itemType(RequestItemType.INTEGER).key("cityId").build());
        return items;
    }

}


