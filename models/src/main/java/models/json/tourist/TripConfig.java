package models.json.tourist;

import lombok.Data;
import models.enums.StayMethod;
import models.enums.TravelMethod;
import java.util.List;
import java.util.Map;

@Data
public class TripConfig {
    private Long cityId;
    private Long numberOfPassengers;
    private StayMethod stayMethod;
    private TravelMethod travelMethod;
    private String onboardingLocation;
    private Map<String, List<String>> templeAndPujasMap; // templeName -> list of puja names
    private List<Long> nearbyPlaces; // List of place IDs
    private StayData stayData;
    private TravelData travelData;
    private List<PassengerDetails> passengerDetails;
}


