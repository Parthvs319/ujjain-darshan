package helpers.blueprint.enums;
public enum RequestEvent {

    REQUESTFAILED("REQUESTFAILED"),
    AUTHENTICATIONFAILED("AUTHENTICATIONFAILED"),
    SERVERERROR("SERVERERROR"),
    PROCESS("PROCESS"),
    APIERROR("APIERROR"),
    AUTHENTICATIONDONE("AUTHENTICATIONDONE"),
    PRECONDITIONFAILED("PRECONDITIONFAILED");


    String dbValue;

    RequestEvent(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getValue() {
        return dbValue;
    }
}
