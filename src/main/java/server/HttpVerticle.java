package src;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import src.user.UserRouter;

public class HttpVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.DELETE)
                .allowedHeader("Content-Type")
                .allowedHeader("Authorization"));

        router.route().handler(BodyHandler.create());

        router.get("/health").handler(ctx -> ctx.response().end("OK"));

        router.mountSubRouter("/user", UserRouter.INSTANCE.router(vertx));
        //router.mountSubRouter("/temple", MountTemple.INSTANCE.router(vertx));

        HttpServerOptions options = new HttpServerOptions().setCompressionSupported(true);
        // Default change it accordingly
        vertx.createHttpServer(options)
                .requestHandler(router)
                .listen(8080);
    }
}
