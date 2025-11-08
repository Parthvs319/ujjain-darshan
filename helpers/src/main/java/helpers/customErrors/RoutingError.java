package helpers.customErrors;

import helpers.blueprint.RoutingEvent;

public class RoutingError extends RuntimeException {

    private int statusCode=409;
    private RoutingEvent event;

    public RoutingError(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }


    public RoutingError(String message,RoutingEvent event, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.event = event;
    }

    public RoutingError(String message) {
        super(message);
    }

    public RoutingError(int statusCode, String message) {
        super(message);
        if(statusCode==401){
            event = RoutingEvent.AUTHENTICATIONFAILED;
        }
        this.statusCode = statusCode;
    }

    public RoutingError(int statusCode, RoutingEvent event,String message) {
        super(message);
        this.statusCode = statusCode;
        this.event = event;
    }

    public RoutingEvent getEvent() {
        return event;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
