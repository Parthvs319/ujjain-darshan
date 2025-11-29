package models.enums;

import io.ebean.annotation.DbEnumValue;

public enum RoomType {

    SINGLE("SINGLE"),
    STANDARD("STANDARD"),
    DOUBLE("DOUBLE"),
    TWIN("TWIN"),
    TRIPLE("TRIPLE"),
    QUAD("QUAD"),
    DELUXE("DELUXE"),
    SUPER_DELUXE("SUPER_DELUXE"),
    SUITE("SUITE"),
    JUNIOR_SUITE("JUNIOR_SUITE"),
    PRESIDENTIAL_SUITE("PRESIDENTIAL_SUITE");
    private final String dbValue;

    RoomType(String dbValue) {
        this.dbValue = dbValue;
    }

    @DbEnumValue
    public String getValue() {
        return dbValue;
    }
}