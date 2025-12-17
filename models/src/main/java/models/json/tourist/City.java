package models.json.tourist;


import com.google.gson.annotations.Expose;
import lombok.Data;
import models.json.hotel.HotelDetails;
import models.sql.Hotel;

import java.util.List;
import java.util.Map;

@Data
public class City {

    @Expose
    private Long city;
    @Expose
    private String cityName;
    @Expose
    private Long days;
    @Expose
    private String end;
    @Expose
    private String start;
    @Expose
    private Stay stay;
    @Expose
    private String time;
    @Expose
    private Travel travel;
    @Expose
    private Long startTime;
    @Expose
    private Long endTime;
    @Expose
    private Long departureTime;
    @Expose
    private Long arrivalTime;
    @Expose
    private Double journeyHours;
    @Expose
    private Double stayHours;
}

