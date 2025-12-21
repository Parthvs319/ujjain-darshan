package models.enums;

import io.ebean.annotation.DbEnumValue;

public enum RequestType {
    DRIVER("DRIVER"),
    PUJARI("PUJARI");

    private final String dbValue;

    RequestType(String dbValue) {
        this.dbValue = dbValue;
    }

    @DbEnumValue
    public String getValue() {
        return dbValue;
    }
}

