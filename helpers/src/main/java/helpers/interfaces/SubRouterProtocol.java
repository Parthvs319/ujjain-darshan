package helpers.interfaces;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public interface SubRouterProtocol {

    Router router(Vertx vertx);

}
