import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.CorsHandler;
import user.UserRouter;

public class HttpVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        Vertx rxVertx = Vertx.newInstance(vertx);

        Router router = Router.router(rxVertx);

        router.route().handler(
                CorsHandler.create("*")
                        .allowedMethod(HttpMethod.GET)
                        .allowedMethod(HttpMethod.POST)
                        .allowedMethod(HttpMethod.PUT)
                        .allowedMethod(HttpMethod.DELETE)
                        .allowedHeader("Content-Type")
                        .allowedHeader("Authorization")
        );

        router.route().handler(BodyHandler.create());

        router.get("/health").handler(ctx -> ctx.response().end("OK"));

        router.mountSubRouter("/user", UserRouter.INSTANCE.router(rxVertx));

        HttpServerOptions options = new HttpServerOptions().setCompressionSupported(true);

        rxVertx.createHttpServer(options)
                .requestHandler(router)
                .rxListen(8080)
                .subscribe(server -> {
                    System.out.println("HTTP Server started on port 8080");
                }, throwable -> {
                    System.err.println("Failed to start HTTP Server: " + throwable.getMessage());
                });
    }
}