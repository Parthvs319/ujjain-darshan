package helpers.customErrors;

import helpers.blueprint.enums.RequestEvent;
import helpers.blueprint.enums.RoutingEvent;

public class RoutingError extends RuntimeException {

    private int statusCode=409;
    private RequestEvent event;

    public RoutingError(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public RoutingError(String message, RequestEvent event, int statusCode) {
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
            event = RequestEvent.AUTHENTICATIONFAILED;
        }
        this.statusCode = statusCode;
    }

    public RoutingError(int statusCode, RequestEvent event,String message) {
        super(message);
        this.statusCode = statusCode;
        this.event = event;
    }

    public RequestEvent getEvent() {
        return event;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
