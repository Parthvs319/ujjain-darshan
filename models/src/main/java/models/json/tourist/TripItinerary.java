package models.json.tourist;

import lombok.Data;
import java.util.List;

@Data
public class TripItinerary {
    private Long date; // timestamp
    private List<ItineraryItem> items;
    
    @Data
    public static class ItineraryItem {
        private String type; // "temple", "puja", "hotel", "travel"
        private Long entityId;
        private String name;
        private Long startTime;
        private Long endTime;
        private String description;
    }
}


