package hotel;


import helpers.interfaces.SubRouterProtocol;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;

public enum HotelRouter implements SubRouterProtocol {

    INSTANCE;

    @Override
    public Router router(Vertx vertx) {
        Router router = Router.router(vertx);
        router.get("/getAllHotels").handler(GetAllHotelsController.INSTANCE::handle);
        router.get("/:id/getAllHotelsByUser").handler(GetAllHotelsByUserController.INSTANCE::handle);
        router.post("/listProperty").handler(ListPropertyController.INSTANCE::handle);
        router.delete("/:id/remove").handler(RemoveHotelController.INSTANCE::handle);
        router.post("/:id/verifyByAdmin").handler(VerifyHotelDetailsController.INSTANCE::handle);
        //  router.post("/updateRoomRates").handler(UpdateHotelController.INSTANCE::handle);
        router.post("/activate").handler(ActivateHotelController.INSTANCE::handle);


        return router;
    }
}

