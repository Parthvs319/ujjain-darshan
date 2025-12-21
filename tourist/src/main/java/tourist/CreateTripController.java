package tourist;

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
import models.enums.StayMethod;
import models.enums.TravelMethod;
import models.enums.UserType;
import models.enums.VehicleSeatingType;
import models.json.tourist.CreateTripData;
import models.json.tourist.StayData;
import models.json.tourist.TravelData;
import models.json.tourist.TripConfig;
import models.json.vehicles.VehicleDetails;
import models.repos.*;
import models.sql.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@UserAnnotation
public enum CreateTripController implements BaseController {

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
        if (!request.getUser().getUserType().equals(UserType.TOURIST)) {
            throw new RoutingError("You are not permitted to create trips!");
        }

        CreateTripData tripData = request.getRequest().get("details");
        if (tripData == null) {
            throw new RoutingError("Trip details are required!");
        }

        if (tripData.getCityId() == null || tripData.getCityId() == 0L) {
            throw new RoutingError("City ID is required!");
        }
        if (tripData.getStartDate() == null || tripData.getEndDate() == null) {
            throw new RoutingError("Start date and end date are required!");
        }
        if (tripData.getStartDate() >= tripData.getEndDate()) {
            throw new RoutingError("End date must be after start date!");
        }

        City city = CityRepository.INSTANCE.exprFinder().eq("id", tripData.getCityId()).findOne();
        if (city == null) {
            throw new RoutingError("Invalid city ID!");
        }

        long days = (tripData.getEndDate() - tripData.getStartDate()) / (24 * 60 * 60 * 1000) + 1;

        Trip trip = new Trip();
        trip.setUser(request.getUser());
        trip.setCity(city);
        trip.setStartDate(new Timestamp(tripData.getStartDate()));
        trip.setEndDate(new Timestamp(tripData.getEndDate()));
        trip.setTripId("TRIP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        trip.setTitle("Trip to " + city.getName());
        trip.setStatus(Status.PENDING);

        TripConfig config = new TripConfig();
        config.setCityId(tripData.getCityId());
        config.setStayMethod(tripData.getStayData() != null && tripData.getStayData().getStayMethod() != null
                ? tripData.getStayData().getStayMethod()
                : StayMethod.SELF);
        config.setTravelMethod(tripData.getTravelData() != null && tripData.getTravelData().getTravelMethod() != null
                ? tripData.getTravelData().getTravelMethod()
                : TravelMethod.SELF);
        config.setOnboardingLocation(tripData.getOnboardingLocation());
        config.setTempleAndPujasMap(tripData.getTempleAndPujasMap());
        config.setStayData(tripData.getStayData());
        config.setTravelData(tripData.getTravelData());
        config.setPassengerDetails(tripData.getPassengerDetailsList());

        int numberOfPassengers = tripData.getPassengerDetailsList() != null
                ? tripData.getPassengerDetailsList().size()
                : 1;
        config.setNumberOfPassengers((long) numberOfPassengers);
        trip.setNumberOfPassengers(numberOfPassengers);

        double totalBudget = 0.0;

        if (tripData.getStayData() != null &&
                StayMethod.VIA_TEMPLE_TRAILS.equals(tripData.getStayData().getStayMethod()) &&
                tripData.getStayData().getHotelBooking() != null) {
            StayData.HotelBooking hotelBooking = tripData.getStayData().getHotelBooking();
            if (hotelBooking.getHotelId() != null) {
                Hotel hotel = HotelRepository.INSTANCE.exprFinder().eq("id", hotelBooking.getHotelId()).findOne();
                if (hotel == null) {
                    throw new RoutingError("Invalid hotel ID!");
                }
                if (hotelBooking.getPricePerRoom() != null && hotelBooking.getNumberOfRooms() != null) {
                    totalBudget += hotelBooking.getPricePerRoom() * hotelBooking.getNumberOfRooms() * days;
                }
            }
        }

        if (tripData.getTravelData() != null &&
                TravelMethod.VIA_TEMPLE_TRAILS.equals(tripData.getTravelData().getTravelMethod()) &&
                tripData.getTravelData().getVehicles() != null) {
            for (TravelData.VehicleBooking vehicleBooking : tripData.getTravelData().getVehicles()) {
                if (vehicleBooking.getPricePerVehicle() != null && vehicleBooking.getNumberOfVehicles() != null) {
                    totalBudget += vehicleBooking.getPricePerVehicle() * vehicleBooking.getNumberOfVehicles() * days;
                }
            }
            if (tripData.getTravelData().getTotalPrice() != null) {
                totalBudget += tripData.getTravelData().getTotalPrice();
            }
        }

        trip.setBudget((long) totalBudget);
        trip.setConfig(config);
        trip.save();

        if (tripData.getTravelData() != null &&
                TravelMethod.VIA_TEMPLE_TRAILS.equals(tripData.getTravelData().getTravelMethod()) &&
                tripData.getTravelData().getVehicles() != null) {

            List<VehicleSeatingType> requestedVehicleTypes = tripData.getTravelData().getVehicles().stream()
                    .map(TravelData.VehicleBooking::getVehicleType)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            if (requestedVehicleTypes.isEmpty()) {
                throw new RoutingError("Vehicle types are required for travel booking!");
            }

            List<Drivers> allDrivers = DriverRepository.INSTANCE.exprFinder()
                    .eq("city.id", city.getId())
                    .eq("status", Status.APPROVED)
                    .findList();

            List<Drivers> eligibleDrivers = allDrivers.stream()
                    .filter(driver -> {
                        List<Vehicles> driverVehicles = VehiclesRepository.INSTANCE.exprFinder()
                                .eq("driver.id", driver.getId())
                                .eq("status", Status.APPROVED)
                                .findList();

                        if (driverVehicles.isEmpty()) {
                            return false;
                        }

                        for (Vehicles vehicle : driverVehicles) {
                            VehicleDetails details = vehicle.getDetails();
                            if (details != null && details.getVehicleType() != null) {
                                String vehicleType = details.getVehicleType();

                                for (VehicleSeatingType requestedType : requestedVehicleTypes) {
                                    if (matchesVehicleType(vehicleType, requestedType)) {
                                        return true;
                                    }
                                }
                            }
                        }

                        return false;
                    })
                    .collect(Collectors.toList());

            for (Drivers driver : eligibleDrivers) {
                TripRequest tripRequest = new TripRequest();
                tripRequest.setTrip(trip);
                tripRequest.setDriver(driver);
                tripRequest.setVehicle(null);
                tripRequest.setRequestType(RequestType.DRIVER);
                tripRequest.setStatus(Status.PENDING);
                tripRequest.save();
            }
        }

        if (tripData.getTempleAndPujasMap() != null && !tripData.getTempleAndPujasMap().isEmpty()) {
            List<Pujari> availablePujaris = PujariRepository.INSTANCE.exprFinder()
                    .eq("city.id", city.getId())
                    .eq("status", Status.APPROVED)
                    .findList();

            for (String templeName : tripData.getTempleAndPujasMap().keySet()) {
                List<String> pujaNames = tripData.getTempleAndPujasMap().get(templeName);

                for (Pujari pujari : availablePujaris) {
                    TripRequest tripRequest = new TripRequest();
                    tripRequest.setTrip(trip);
                    tripRequest.setPujari(pujari);
                    tripRequest.setRequestType(RequestType.PUJARI);
                    tripRequest.setTempleName(templeName);
                    tripRequest.setPujaNames(new ArrayList<>(pujaNames));
                    tripRequest.setStatus(Status.PENDING);
                    tripRequest.save();
                }
            }
        }

        return new SuccessResponse(true, "Trip created successfully! Trip ID: " + trip.getTripId());
    }

    private boolean matchesVehicleType(String vehicleType, VehicleSeatingType requestedType) {
        if (vehicleType == null || requestedType == null) {
            return false;
        }
        
        String vehicleTypeLower = vehicleType.toLowerCase();
        String requestedTypeValue = requestedType.getValue().toLowerCase();
        
        if (vehicleTypeLower.equals(requestedTypeValue)) {
            return true;
        }
        
        boolean isRequestedFourWheeler = requestedTypeValue.contains("seater");
        
        boolean isVehicleFourWheeler = vehicleTypeLower.contains("fourwheeler") ||
                                        vehicleTypeLower.equals("car") ||
                                        vehicleTypeLower.equals("suv") ||
                                        vehicleTypeLower.equals("sedan") ||
                                        vehicleTypeLower.equals("hatchback") ||
                                        vehicleTypeLower.contains("seater");
        
        if (isRequestedFourWheeler && isVehicleFourWheeler) {
            return true;
        }
        
        return false;
    }

    public List<RequestItem> items() {
        List<RequestItem> items = new ArrayList<>();
        items.add(RequestItem.builder().key("cityId").itemType(RequestItemType.INTEGER).required(true).build());
        items.add(RequestItem.builder().key("details").itemType(RequestItemType.OBJECT).objectClass(CreateTripData.class).required(true).build());
        return items;
    }
}
