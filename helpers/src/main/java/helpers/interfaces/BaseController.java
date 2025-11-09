package helpers.interfaces;

import helpers.customErrors.RoutingError;
import io.vertx.ext.web.RoutingContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

@FunctionalInterface
public interface BaseController {


    default void fail(String e) throws RoutingError {
        throw new RoutingError(e);
    }

    default String requestId(RoutingContext rc) {
        if (rc.request().headers().contains("request_id"))
            return rc.request().headers().get("request_id");
        return null;
    }

    default void printStackAndThrow(Exception e) throws RoutingError {
        Writer writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        System.out.println(writer.toString());
        throw new RoutingError(e.getMessage());
    }

    default void printStack(Exception e) throws RoutingError {
        Writer writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        System.out.println(writer.toString());
    }

    default void printStackAndThrow(String e) {
        System.out.println(e);
        throw new RoutingError(e);
    }

    default void printStack(String e) {
        System.out.println(e);
    }

    void handle(io.vertx.rxjava.ext.web.RoutingContext event);
}
