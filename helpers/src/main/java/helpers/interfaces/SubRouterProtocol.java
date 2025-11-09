package helpers.interfaces;

import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;

public interface SubRouterProtocol {

    Router router(Vertx vertx);

}
