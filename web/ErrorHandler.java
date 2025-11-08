package src.web;

import io.vertx.ext.web.RoutingContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import src.web.exceptions.BadRequestException;
import src.web.exceptions.NotFoundException;
import src.web.exceptions.UnauthorizedException;

public class ErrorHandler {
    private static ObjectMapper mapper = new ObjectMapper();

    public static void handle(RoutingContext ctx) {
        Throwable failure = ctx.failure();
        int status = 500;
        String message = "internal_error";
        if (failure instanceof BadRequestException) {
            status = 400;
            message = failure.getMessage();
        } else if (failure instanceof UnauthorizedException) {
            status = 401;
            message = failure.getMessage();
        } else if (failure instanceof NotFoundException) {
            status = 404;
            message = failure.getMessage();
        } else if (failure != null) {
            message = failure.getMessage();
        }
        ctx.response().setStatusCode(status).putHeader("Content-Type", "application/json");
//        try {
//            ctx.response().end(mapper.writeValueAsString(Map.of("error", message)));
//        } catch (Exception e) {
//            ctx.response().end("{"error":"" + message + ""}");
//        }
    }
}
