package models.enums;

import io.ebean.annotation.DbEnumValue;

public enum FuelType {
    PETROL("PETROL"),
    CNG("CNG"),
    EV("EV"),
    DIESEL("DIESEL");

    String dbValue;

    FuelType(String dbValue) {
        this.dbValue = dbValue;
    }

    @DbEnumValue
    public String getValue() {
        return dbValue;
    }
}
