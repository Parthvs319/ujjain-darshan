package driver;

import helpers.annotations.UserAnnotation;
import helpers.customErrors.RoutingError;
import helpers.interfaces.BaseController;
import helpers.utils.ResponseUtils;
import io.vertx.rxjava.ext.web.RoutingContext;
import lombok.Data;
import models.access.middlewear.user.UserAccessMiddleware;
import models.body.UserLoginRequest;
import models.enums.Status;
import models.enums.UserType;
import models.json.pujari.PujariDetails;
import models.json.vehicles.DriverOnboardingDetails;
import models.repos.DriverRepository;
import models.sql.Drivers;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UserAnnotation
public enum GetAllDriversController implements BaseController {

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
            if(!request.getUser().getUserType().equals(UserType.ADMIN)) {
                throw new RoutingError("You can not access this data !");
            }
            List<Drivers> drivers = DriverRepository.INSTANCE.exprFinder().findList();
            response.setResponse(drivers);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RoutingError(e.getMessage());
        }
        return response;
    }

    @Data
    class Response {
        private List<DriverDTO> drivers = new ArrayList<>();

        private void setResponse(List<models.sql.Drivers> drivers) {
            this.drivers = drivers.stream()
                    .map(DriverDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    class DriverDTO {
        private Long id;
        private String name;
        private String city;
        private String status;
        private DriverOnboardingDetails details;

        DriverDTO(models.sql.Drivers driver) {
            this.id = driver.getId();
            this.status = driver.getStatus().getValue();
            this.name = driver.getUser().getName();
            this.city = driver.getCity().getName();
            this.details = driver.getDetails();
        }
    }
}


