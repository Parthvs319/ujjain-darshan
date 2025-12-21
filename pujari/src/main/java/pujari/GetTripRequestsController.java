package pujari;

import helpers.annotations.UserAnnotation;
import helpers.customErrors.RoutingError;
import helpers.interfaces.BaseController;
import helpers.utils.ResponseUtils;
import io.vertx.rxjava.ext.web.RoutingContext;
import lombok.Data;
import models.access.middlewear.user.UserAccessMiddleware;
import models.body.UserLoginRequest;
import models.enums.UserType;
import models.repos.PujariRepository;
import models.repos.TripRequestRepository;
import models.sql.Pujari;
import models.sql.TripRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UserAnnotation
public enum GetTripRequestsController implements BaseController {

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
        if (!request.getUser().getUserType().equals(UserType.PUJARI)) {
            throw new RoutingError("You are not permitted to access trip requests!");
        }

        // Find the pujari for this user
        Pujari pujari = PujariRepository.INSTANCE.exprFinder()
                .eq("user.id", request.getUser().getId())
                .findOne();
        
        if (pujari == null) {
            throw new RoutingError("Pujari profile not found!");
        }

        // Get all pending trip requests for this pujari
        List<TripRequest> tripRequests = TripRequestRepository.INSTANCE.findPendingByPujari(pujari);

        Response response = new Response();
        response.setRequests(tripRequests.stream()
                .map(TripRequestDTO::new)
                .collect(Collectors.toList()));
        return response;
    }

    @Data
    class Response {
        private List<TripRequestDTO> requests = new ArrayList<>();
    }

    @Data
    class TripRequestDTO {
        private Long id;
        private String tripId;
        private String cityName;
        private Long startDate;
        private Long endDate;
        private Integer numberOfPassengers;
        private String templeName;
        private List<String> pujaNames; // All pujas for this temple
        private Long budget;

        TripRequestDTO(TripRequest request) {
            this.id = request.getId();
            if (request.getTrip() != null) {
                this.tripId = request.getTrip().getTripId();
                this.cityName = request.getTrip().getCity() != null ? request.getTrip().getCity().getName() : null;
                this.startDate = request.getTrip().getStartDate() != null ? request.getTrip().getStartDate().getTime() : null;
                this.endDate = request.getTrip().getEndDate() != null ? request.getTrip().getEndDate().getTime() : null;
                this.numberOfPassengers = request.getTrip().getNumberOfPassengers();
                this.budget = request.getTrip().getBudget();
            }
            this.templeName = request.getTempleName();
            this.pujaNames = request.getPujaNames() != null ? new ArrayList<>(request.getPujaNames()) : new ArrayList<>();
        }
    }
}

