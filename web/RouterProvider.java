package src.web;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import src.web.handlers.AuthHandler;
import src.web.handlers.TripHandler;
import src.web.handlers.TempleHandler;
import src.web.handlers.HotelHandler;
import src.web.handlers.AuthMiddleware;

public class RouterProvider {
    public static Router createRouter(Vertx vertx) {
        Router router = Router.router(vertx);

        router.route("/api/v1/*").handler(io.vertx.ext.web.handler.BodyHandler.create());

        // Auth
        router.post("/api/v1/auth/signup").handler(AuthHandler::signUp);
        router.post("/api/v1/auth/verify-otp").handler(AuthHandler::verifyOtp);
        router.post("/api/v1/auth/login").handler(AuthHandler::login);

        // Public listings
        router.get("/api/v1/temples").handler(TempleHandler::list);
        router.get("/api/v1/hotels").handler(HotelHandler::list);
//        router.get("/api/v1/cities").handler(CityHandler::list);

        // Protected trips (require auth)
        router.post("/api/v1/trips").handler(AuthMiddleware::requireAuth).handler(TripHandler::createTrip);
        router.get("/api/v1/trips/:id").handler(AuthMiddleware::requireAuth).handler(TripHandler::getTrip);

        // Global failure handler
        router.route().failureHandler(ErrorHandler::handle);

        return router;
    }
}
