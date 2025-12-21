package models.json.hotel;

import lombok.Data;
import models.enums.RoomType;

import java.util.HashMap;
import java.util.Map;

@Data
public class Room {

    private String title;

    private RoomType roomType = RoomType.SINGLE;

    private Long numberOfRooms = 0L;

    private Double currentPrice = 0D;

    // Map of date and price for a room
    private Map<String , Double> roomPricePerNightMap = new HashMap<>();

}
