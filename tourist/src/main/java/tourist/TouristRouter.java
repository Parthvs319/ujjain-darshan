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



//        router.post("/completeOnboarding").handler(CompleteDriverOnboardingController.INSTANCE::handle);
//        router.get("/getOnboardingDetails").handler(GetDriverOnboardingDetailsController.INSTANCE::handle);
//        router.get("/getAllDrivers").handler(GetAllDriversController.INSTANCE::handle);
//        router.post("/approve/driver").handler(UpdateDriverStatusController.INSTANCE::handle);
//        router.get("/approve/vehicle").handler(UpdateVehicleStatusController.INSTANCE::handle);
//
//        router.get("/getAllVehicles").handler(GetAllVehiclesController.INSTANCE::handle);
//        router.get("/:id/getAllVehiclesByUser").handler(GetAllVehiclesByUserController.INSTANCE::handle);
//        router.post("/addVehicle").handler(AddVehicleController.INSTANCE::handle);
//        router.delete("/:id/remove").handler(RemoveVehicleController.INSTANCE::handle);

        return router;
    }
}



