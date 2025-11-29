package models.json;

import lombok.Data;
import models.enums.HotelType;
import models.enums.UserType;

import java.util.List;

@Data
public class HotelDetails {

    private UserType deactivatedBy;

    private Long deactivatedById;

    private HotelType type;

    private String propertyName;

    private Long rating;

    private Long propertyBuildDate;

    private String acceptingBookingSince;

    private boolean channelManager;

    private String email;// OTP VERIFICATION

    private String mobileNumber;// OTP VERIFICATION

    private String whatsAppMobileNumber; // OTP VERIFICATION

    private String landline;

    private String pincode ;

    private String state;

    private String country;

    private String locality;

    private Amenities amenities;

    private List<Room> rooms;
}
