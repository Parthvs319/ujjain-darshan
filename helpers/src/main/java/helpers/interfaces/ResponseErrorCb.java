package helpers.interfaces;

import io.vertx.rxjava.ext.web.RoutingContext;

public interface ResponseErrorCb {

    void handle(Throwable error, RoutingContext routingContext);

}
