package models.json.tourist;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Data
public class CreateTripData {

    private Long cityId = 0L;

    private Long startDate;

    private Long endDate;

    private StayData stayData;

    private TravelData travelData;

    private Map<String , List<String>> templeAndPujasMap;

    private String onboardingLocation;

    private List<PassengerDetails> passengerDetailsList;
}
