package models.json;

import lombok.Data;
import models.enums.UserType;

@Data
public class HotelDetails {

    private UserType deactivatedBy;

    private Long deactivatedById;





}
