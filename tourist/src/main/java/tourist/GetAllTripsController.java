package tourist;

import helpers.annotations.UserAnnotation;
import helpers.customErrors.RoutingError;
import helpers.interfaces.BaseController;
import helpers.utils.ResponseUtils;
import io.vertx.rxjava.ext.web.RoutingContext;
import lombok.Data;
import models.access.middlewear.user.UserAccessMiddleware;
import models.body.UserLoginRequest;
import models.enums.UserType;
import models.repos.TripsRepository;
import models.sql.Trip;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UserAnnotation
public enum GetAllTripsController implements BaseController {

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
        List<Trip> trips;
        
        if (request.getUser().getUserType().equals(UserType.TOURIST)) {
            trips = TripsRepository.INSTANCE.findByUserId(request.getUser().getId());
        } else if (request.getUser().getUserType().equals(UserType.ADMIN)) {
            trips = TripsRepository.INSTANCE.finder();
        } else {
            throw new RoutingError("You are not permitted to access this data!");
        }

        Response response = new Response();
        response.setTrips(trips.stream()
                .map(TripDTO::new)
                .collect(Collectors.toList()));
        return response;
    }

    @Data
    class Response {
        private List<TripDTO> trips = new ArrayList<>();
    }

    @Data
    class TripDTO {
        private Long id;
        private String tripId;
        private String title;
        private Long startDate;
        private Long endDate;
        private String cityName;
        private Integer numberOfPassengers;
        private Long budget;
        private Long used;
        private String status;
        private String stayMethod;
        private String travelMethod;
        private String assignedDriverName;
        private String assignedPujariName;

        TripDTO(Trip trip) {
            this.id = trip.getId();
            this.tripId = trip.getTripId();
            this.title = trip.getTitle();
            this.startDate = trip.getStartDate() != null ? trip.getStartDate().getTime() : null;
            this.endDate = trip.getEndDate() != null ? trip.getEndDate().getTime() : null;
            this.cityName = trip.getCity() != null ? trip.getCity().getName() : null;
            this.numberOfPassengers = trip.getNumberOfPassengers();
            this.budget = trip.getBudget();
            this.used = trip.getUsed();
            this.status = trip.getStatus() != null ? trip.getStatus().getValue() : null;
            
            if (trip.getConfig() != null) {
                this.stayMethod = trip.getConfig().getStayMethod().toString();
                this.travelMethod = trip.getConfig().getTravelMethod().toString();
            }
            
            if (trip.getAssignedDriver() != null && trip.getAssignedDriver().getUser() != null) {
                this.assignedDriverName = trip.getAssignedDriver().getUser().getName();
            }
            
            if (trip.getAssignedPujari() != null && trip.getAssignedPujari().getUser() != null) {
                this.assignedPujariName = trip.getAssignedPujari().getUser().getName();
            }
        }
    }
}


