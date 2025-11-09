package models.enums;

import io.ebean.annotation.DbEnumValue;

public enum UserType {
    TOURIST("TOURIST"),
    PUJARI("PUJARI"),
    GUIDE("GUIDE"),
    DRIVER("DRIVER"),
    TEMPLE_ADMIN("TEMPLE_ADMIN"),
    HOTEL_ADMIN("HOTEL_ADMIN"),
    ADMIN("ADMIN");


    String dbValue;

    UserType(String dbValue) {
        this.dbValue = dbValue;
    }

    @DbEnumValue
    public String getValue() {
        return dbValue;
    }
}
