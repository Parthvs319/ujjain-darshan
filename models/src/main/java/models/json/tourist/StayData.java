package models.json.tourist;

import lombok.Data;
import models.enums.StayMethod;

@Data
public class StayData {
    private StayMethod stayMethod;
    private HotelBooking hotelBooking;
    
    @Data
    public static class HotelBooking {
        private Long hotelId;
        private Integer numberOfRooms;
        private Double pricePerRoom;
        private String roomType;
    }
}
