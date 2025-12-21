package models.enums;

import io.ebean.annotation.DbEnumValue;

public enum StayMethod {
    SELF("self"),
    VIA_TEMPLE_TRAILS("via_temple_trails");

    private final String dbValue;

    StayMethod(String dbValue) {
        this.dbValue = dbValue;
    }

    @DbEnumValue
    public String getValue() {
        return dbValue;
    }
    
    public static StayMethod fromString(String value) {
        if (value == null) return SELF;
        for (StayMethod method : values()) {
            if (method.dbValue.equalsIgnoreCase(value)) {
                return method;
            }
        }
        return SELF; // Default
    }
}

