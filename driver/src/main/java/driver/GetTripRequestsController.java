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
import models.json.tourist.TravelData;
import models.repos.DriverRepository;
import models.repos.TripRequestRepository;
import models.repos.VehiclesRepository;
import models.sql.Drivers;
import models.sql.TripRequest;
import models.sql.Vehicles;

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
        if (!request.getUser().getUserType().equals(UserType.DRIVER)) {
            throw new RoutingError("You are not permitted to access trip requests!");
        }

        // Find the driver for this user
        Drivers driver = DriverRepository.INSTANCE.exprFinder()
                .eq("user.id", request.getUser().getId())
                .findOne();
        
        if (driver == null) {
            throw new RoutingError("Driver profile not found!");
        }

        // Get all pending trip requests for this driver
        List<TripRequest> tripRequests = TripRequestRepository.INSTANCE.findPendingByDriver(driver);

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
        private String onboardingLocation;
        private Long budget;
        private List<VehicleRequirement> vehicleRequirements;
        private List<AvailableVehicle> availableVehicles; // Driver's vehicles that can be used

        TripRequestDTO(TripRequest request) {
            this.id = request.getId();
            if (request.getTrip() != null) {
                this.tripId = request.getTrip().getTripId();
                this.cityName = request.getTrip().getCity() != null ? request.getTrip().getCity().getName() : null;
                this.startDate = request.getTrip().getStartDate() != null ? request.getTrip().getStartDate().getTime() : null;
                this.endDate = request.getTrip().getEndDate() != null ? request.getTrip().getEndDate().getTime() : null;
                this.numberOfPassengers = request.getTrip().getNumberOfPassengers();
                this.budget = request.getTrip().getBudget();
                if (request.getTrip().getConfig() != null) {
                    this.onboardingLocation = request.getTrip().getConfig().getOnboardingLocation();
                    
                    // Extract vehicle requirements from trip config
                    TravelData travelData = request.getTrip().getConfig().getTravelData();
                    if (travelData != null && travelData.getVehicles() != null) {
                        this.vehicleRequirements = travelData.getVehicles().stream()
                                .map(vb -> {
                                    VehicleRequirement vr = new VehicleRequirement();
                                    vr.setVehicleType(vb.getVehicleType() != null ? vb.getVehicleType().getValue() : null);
                                    vr.setNumberOfVehicles(vb.getNumberOfVehicles());
                                    vr.setPricePerVehicle(vb.getPricePerVehicle());
                                    return vr;
                                })
                                .collect(Collectors.toList());
                    }
                }
            }
            
            // Get driver's available vehicles
            if (request.getDriver() != null) {
                List<Vehicles> driverVehicles = VehiclesRepository.INSTANCE.exprFinder()
                        .eq("driver.id", request.getDriver().getId())
                        .eq("status", models.enums.Status.APPROVED)
                        .findList();
                
                this.availableVehicles = driverVehicles.stream()
                        .map(v -> {
                            AvailableVehicle av = new AvailableVehicle();
                            av.setId(v.getId());
                            av.setNumber(v.getNumber());
                            av.setSeatingCapacity(v.getDetails() != null ? v.getDetails().getSeatingCapacity() : null);
                            return av;
                        })
                        .collect(Collectors.toList());
            }
        }
    }
    
    @Data
    class VehicleRequirement {
        private String vehicleType;
        private Integer numberOfVehicles;
        private Double pricePerVehicle;
    }
    
    @Data
    class AvailableVehicle {
        private Long id;
        private String number;
        private Integer seatingCapacity;
    }
}

