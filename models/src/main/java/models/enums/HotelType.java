package models.enums;

import io.ebean.annotation.DbEnumValue;

public enum HotelType {
    HOTEL("HOTEL"),
    LODGE("LODGE"),
    RESORT("RESORT"),
    GUEST_HOUSE("GUEST_HOUSE"),
    HOME_STAY("HOME_STAY");


    String dbValue;

    HotelType(String dbValue) {
        this.dbValue = dbValue;
    }

    @DbEnumValue
    public String getValue() {
        return dbValue;
    }
}
