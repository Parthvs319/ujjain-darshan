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
import models.enums.RequestType;
import models.enums.Status;
import models.enums.UserType;
import models.repos.DriverRepository;
import models.repos.TripRequestRepository;
import models.repos.VehiclesRepository;
import models.sql.Drivers;
import models.sql.TripRequest;
import models.sql.Vehicles;

import java.util.ArrayList;
import java.util.List;

@UserAnnotation
public enum AcceptTripRequestController implements BaseController {

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
        if (!request.getUser().getUserType().equals(UserType.DRIVER)) {
            throw new RoutingError("You are not permitted to accept trip requests!");
        }

        Long requestId = request.getRequest().get("requestId");
        if (requestId == null) {
            throw new RoutingError("Request ID is required!");
        }
        
        // Optional: Driver can specify which vehicle(s) to use (can be a list of vehicle IDs)
        List<Long> vehicleIds = request.getRequest().get("vehicleIds"); // Optional list of vehicle IDs

        // Find the driver for this user
        Drivers driver = DriverRepository.INSTANCE.exprFinder()
                .eq("user.id", request.getUser().getId())
                .findOne();
        
        if (driver == null) {
            throw new RoutingError("Driver profile not found!");
        }

        // Find the trip request
        TripRequest tripRequest = TripRequestRepository.INSTANCE.exprFinder()
                .eq("id", requestId)
                .eq("driver.id", driver.getId())
                .eq("requestType", RequestType.DRIVER)
                .findOne();

        if (tripRequest == null) {
            throw new RoutingError("Trip request not found or you don't have permission to accept it!");
        }

        if (tripRequest.getStatus() != Status.PENDING) {
            throw new RoutingError("This trip request has already been " + tripRequest.getStatus().getValue().toLowerCase() + "!");
        }

        // Check if trip already has an assigned driver
        if (tripRequest.getTrip().getAssignedDriver() != null) {
            // Trip already has a driver, reject this request
            tripRequest.setStatus(Status.REJECTED);
            tripRequest.setRejectionReason("Another driver has already accepted this trip");
            tripRequest.update();
            throw new RoutingError("This trip has already been assigned to another driver!");
        }

        // Validate and set vehicle(s) if provided
        if (vehicleIds != null && !vehicleIds.isEmpty()) {
            // Verify all vehicles belong to this driver and are approved
            for (Long vehicleId : vehicleIds) {
                Vehicles vehicle = VehiclesRepository.INSTANCE.exprFinder()
                        .eq("id", vehicleId)
                        .eq("driver.id", driver.getId())
                        .eq("status", Status.APPROVED)
                        .findOne();
                
                if (vehicle == null) {
                    throw new RoutingError("Vehicle with ID " + vehicleId + " not found or not approved for your account!");
                }
            }
            
            // Set the first vehicle as primary (or we could store all in a separate table)
            // For now, we'll set the first vehicle
            Vehicles primaryVehicle = VehiclesRepository.INSTANCE.exprFinder()
                    .eq("id", vehicleIds.get(0))
                    .findOne();
            tripRequest.setVehicle(primaryVehicle);
        } else {
            // If no vehicle specified, use driver's first approved vehicle
            Vehicles firstVehicle = VehiclesRepository.INSTANCE.exprFinder()
                    .eq("driver.id", driver.getId())
                    .eq("status", Status.APPROVED)
                    .setMaxRows(1)
                    .findOne();
            
            if (firstVehicle == null) {
                throw new RoutingError("You don't have any approved vehicles! Please add a vehicle first.");
            }
            tripRequest.setVehicle(firstVehicle);
        }

        // Accept the request - first to accept wins! (Using APPROVED status)
        tripRequest.setStatus(Status.APPROVED);
        tripRequest.update();

        // Assign driver to the trip
        tripRequest.getTrip().setAssignedDriver(driver);
        tripRequest.getTrip().update();

        // Reject all other pending requests for this trip (same vehicle type requirement)
        List<TripRequest> otherRequests = TripRequestRepository.INSTANCE.exprFinder()
                .eq("trip.id", tripRequest.getTrip().getId())
                .eq("requestType", RequestType.DRIVER)
                .eq("status", models.enums.Status.PENDING)
                .ne("id", tripRequest.getId())
                .findList();

        for (TripRequest otherRequest : otherRequests) {
            otherRequest.setStatus(Status.REJECTED);
            otherRequest.setRejectionReason("Another driver accepted this trip first");
            otherRequest.update();
        }

        return new SuccessResponse(true, "Trip request accepted successfully! You have been assigned to trip: " + tripRequest.getTrip().getTripId());
    }

    public List<RequestItem> items() {
        List<RequestItem> items = new ArrayList<>();
        items.add(RequestItem.builder().key("requestId").itemType(RequestItemType.INTEGER).required(true).build());
        items.add(RequestItem.builder().key("vehicleIds").itemType(RequestItemType.JSONOBJECT).required(false).build());
        return items;
    }
}

