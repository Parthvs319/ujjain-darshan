package models.enums;

import io.ebean.annotation.DbEnumValue;

public enum TravelMethod {
    SELF("self"),
    VIA_TEMPLE_TRAILS("via_temple_trails");

    private final String dbValue;

    TravelMethod(String dbValue) {
        this.dbValue = dbValue;
    }

    @DbEnumValue
    public String getValue() {
        return dbValue;
    }
    
    public static TravelMethod fromString(String value) {
        if (value == null) return SELF;
        for (TravelMethod method : values()) {
            if (method.dbValue.equalsIgnoreCase(value)) {
                return method;
            }
        }
        return SELF; // Default
    }
}

