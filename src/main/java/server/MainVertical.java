package src;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import src.web.RouterProvider;

public class MainVertical extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Router router = RouterProvider.createRouter(vertx);
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(port, ar -> {
                if (ar.succeeded()) {
                    System.out.println("HTTP server started on port " + port);
                    startPromise.complete();
                } else {
                    startPromise.fail(ar.cause());
                }
            });
    }
}
