package tourist;

import helpers.interfaces.SubRouterProtocol;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;

public enum TouristRouter implements SubRouterProtocol {

    INSTANCE;

    @Override
    public Router router(Vertx vertx) {
        Router router = Router.router(vertx);

        router.post("/createTrip").handler(CreateTripController.INSTANCE::handle);
        router.get("/getAllTrips").handler(GetAllTripsController.INSTANCE::handle);
        return router;
    }
}


