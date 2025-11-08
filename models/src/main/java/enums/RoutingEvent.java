package enums;

public enum RoutingEvent {

    REQUESTFAILED("REQUESTFAILED"),
    AUTHENTICATIONFAILED("AUTHENTICATIONFAILED"),
    SERVERERROR("SERVERERROR"),
    PROCESS("PROCESS"),
    APIERROR("APIERROR"),
    AUTHENTICATIONDONE("AUTHENTICATIONDONE"),
    PRECONDITIONFAILED("PRECONDITIONFAILED");


    String dbValue;

    RoutingEvent(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getValue() {
        return dbValue;
    }
}
