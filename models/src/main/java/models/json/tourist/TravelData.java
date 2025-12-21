package models.json.tourist;

import lombok.Data;
import models.enums.TravelMethod;
import models.enums.VehicleSeatingType;
import java.util.List;

@Data
public class TravelData {
    private TravelMethod travelMethod;
    private String onboardingLocation; // "Ujjain Railway Station", "Indore Railway Station", etc.
    private List<VehicleBooking> vehicles;
    private Double totalPrice;
    
    @Data
    public static class VehicleBooking {
        private VehicleSeatingType vehicleType;
        private Integer numberOfVehicles;
        private Double pricePerVehicle;
        private Long vehicleId; // Will be set when driver accepts
    }
}
