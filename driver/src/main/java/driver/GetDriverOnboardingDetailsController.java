package driver;

import helpers.annotations.UserAnnotation;
import helpers.customErrors.RoutingError;
import helpers.interfaces.BaseController;
import helpers.utils.ResponseUtils;
import io.vertx.rxjava.ext.web.RoutingContext;
import lombok.Data;
import models.access.middlewear.user.UserAccessMiddleware;
import models.body.UserLoginRequest;
import models.enums.UserType;
import models.json.vehicles.DriverOnboardingDetails;
import models.repos.DriverRepository;
import models.sql.Drivers;
import java.util.ArrayList;

@UserAnnotation
public enum GetDriverOnboardingDetailsController implements BaseController {

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

    private Response map(UserLoginRequest request) {
        Response response = new Response();
        try {
            if(!request.getUser().getUserType().equals(UserType.DRIVER)) {
                throw new RoutingError("You can not complete basic onboarding if you are not a driver !");
            }
            Drivers driver = DriverRepository.INSTANCE.exprFinder().eq("user.id" , request.getUser()).findOne();
            if(driver == null) {
                throw new RoutingError("Basic onboarding of this user is not completed !");
            } else {
                response.setDetails(driver.getDetails());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RoutingError(e.getMessage());
        }
        return response;
    }

    @Data
    class Response {
        private DriverOnboardingDetails details;
    }
}

