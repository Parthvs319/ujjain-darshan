package models.enums;

import io.ebean.annotation.DbEnumValue;

public enum VehicleSeatingType {
    FIVE_SEATER("5_seater"),
    SEVEN_SEATER("7_seater"),
    TEN_SEATER("10_seater"),
    TWELVE_SEATER("12_seater");

    private final String dbValue;

    VehicleSeatingType(String dbValue) {
        this.dbValue = dbValue;
    }

    @DbEnumValue
    public String getValue() {
        return dbValue;
    }
    
    public int getCapacity() {
        switch (this) {
            case FIVE_SEATER: return 5;
            case SEVEN_SEATER: return 7;
            case TEN_SEATER: return 10;
            case TWELVE_SEATER: return 12;
            default: return 5;
        }
    }
    
    public static VehicleSeatingType fromString(String value) {
        if (value == null) return FIVE_SEATER;
        for (VehicleSeatingType type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return FIVE_SEATER; // Default
    }
}

