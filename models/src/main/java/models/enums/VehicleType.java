package models.enums;

import io.ebean.annotation.DbEnumValue;

public enum VehicleType {
    BIKE("BIKE"),
    AUTO("AUTO"),
    FOURWHEELER("FOURWHEELER"),
    FOURWHEELERXL("FOURWHEELERXL");

    String dbValue;

    VehicleType(String dbValue) {
        this.dbValue = dbValue;
    }

    @DbEnumValue
    public String getValue() {
        return dbValue;
    }
}
