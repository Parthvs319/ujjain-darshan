package pujari;

import helpers.interfaces.SubRouterProtocol;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;

public enum PujariRouter implements SubRouterProtocol {

    INSTANCE;

    @Override
    public Router router(Vertx vertx) {
        Router router = Router.router(vertx);
        // Basic Info Filling and Updating that info
        router.post("/completeDetails").handler(AddPujariDetailsController.INSTANCE::handle);
        router.post("/:id/verifyByAdmin").handler(VerifyPujariDetailsController.INSTANCE::handle);
        router.get("/getAllPujari").handler(GetAllPujarisController.INSTANCE::handle);
        router.get("/details").handler(GetAllPujariDetails.INSTANCE::handle);
        return router;
    }
}


