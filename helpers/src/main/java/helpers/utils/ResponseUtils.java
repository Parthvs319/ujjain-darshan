package helpers.utils;

import helpers.customErrors.RoutingError;
import helpers.interfaces.ResponseErrorCb;
import io.vertx.rxjava.ext.web.RoutingContext;
import lombok.Data;

public enum ResponseUtils {

    INSTANCE;

    private ResponseErrorCb callback;

    public void setCallback(ResponseErrorCb callback) {
        this.callback = callback;
    }

    public void writeJsonResponse(RoutingContext routingContext, Object o) {
        try {
            routingContext.response()
                    .putHeader("request-id", requestId(routingContext))
                    .putHeader("content-type", "application/json")
                    .end(Mapper.INSTANCE.getGson().toJson(o));
        } catch (IllegalStateException e) {
            // Silent catch for already closed responses
        }
    }

    public void writeStringResponse(RoutingContext routingContext, String body) {
        if (!routingContext.response().closed()) {
            routingContext.response()
                    .putHeader("request-id", requestId(routingContext))
                    .putHeader("content-type", "application/json")
                    .end(body);
        }
    }

    public void writeError(RoutingContext routingContext, int statusCode, String message) {
        if (!routingContext.response().closed()) {
            routingContext.put("error", message);
            routingContext.response()
                    .putHeader("request-id", requestId(routingContext))
                    .putHeader("content-type", "application/json")
                    .setStatusCode(statusCode)
                    .end(new Error(message).toString());
        }
    }

    boolean notReportable(Throwable error){
        if(error instanceof RoutingError)
            return true;
        if(error.getClass().isAssignableFrom(RoutingError.class))
            return true;
        if(error instanceof NumberFormatException)
            return true;
        return false;
    }

    public void notify(RoutingContext rc, Throwable throwable) {
        try{
            if(callback!=null){
                callback.handle(throwable, rc);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void handleError(RoutingContext rc, Throwable error){
        try {
            if(notReportable(error) && !error.getMessage().toLowerCase().contains("SQLException:")){
                writeError(rc,((RoutingError) error).getStatusCode(),error.getMessage());
//                error.printStackTrace();
            }else{
                notify(rc,error);
                writeError(rc,413,"Technical Error. Please try again later: #");
                error.printStackTrace();
            }
        }catch (Exception e){
            notify(rc,error);
            writeError(rc,409,"Technical Error. Please try again later: #");
//            e.printStackTrace();
        }
    }

    @Data
    class Error {
        private String message;
        private String error;

        public Error(String message) {
            this.message = message;
            this.error = message;
        }

        public String toString() {
            return String.format("{\"message\":\"%s\",\"error\":\"%s\"}", error, message);
        }
    }

    private String requestId(RoutingContext rc) {
        if (rc.request().headers().contains("request_id")) {
            return rc.request().headers().get("request_id");
        }
        return null;
    }
}