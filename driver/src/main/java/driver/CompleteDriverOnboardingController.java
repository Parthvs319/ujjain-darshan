package driver;

import helpers.annotations.UserAnnotation;
import helpers.blueprint.enums.RequestItemType;
import helpers.customErrors.RoutingError;
import helpers.interfaces.BaseController;
import helpers.utils.RequestItem;
import helpers.utils.ResponseUtils;
import helpers.utils.SuccessResponse;
import io.vertx.rxjava.ext.web.RoutingContext;
import lombok.Data;
import models.access.middlewear.user.UserAccessMiddleware;
import models.body.UserLoginRequest;
import models.json.hotel.HotelDetails;
import models.json.vehicles.DriverOnboardingDetails;
import models.repos.CityRepository;
import models.repos.DriverRepository;
import models.repos.VehiclesRepository;
import models.sql.City;
import models.sql.Drivers;
import models.sql.Vehicles;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@UserAnnotation
public enum CompleteDriverOnboardingController implements BaseController {

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
            Drivers driver = DriverRepository.INSTANCE.exprFinder().eq("user.id" , request.getUser()).findOne();
            if(driver == null) {
                driver = new Drivers();
                driver.setUser(request.getUser());
                City city = CityRepository.INSTANCE.citySqlFinder.byId(request.getRequest().get("cityId"));
                if(city == null)
                    throw new RoutingError("Invalid City Id Passed !");
                driver.setCity(city);
                DriverOnboardingDetails details = request.getRequest().get("details");
                if(details == null) {
                    throw new RoutingError("Onboarding details are mandatory !");
                }
                driver.setDetails(details);
                driver.save();
            } else {
                throw new RoutingError("Driver Onboarding Already Completed !");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RoutingError(e.getMessage());
        }
        return new SuccessResponse();
    }

    public List<RequestItem> items() {
        List<helpers.utils.RequestItem> items = new ArrayList<>();
        items.add(RequestItem.builder().key("userId").itemType(RequestItemType.INTEGER).required(false).build());
        items.add(RequestItem.builder().key("details").required(true).itemType(RequestItemType.OBJECT).objectClass(DriverOnboardingDetails.class).build());
        return items;
    }
}
