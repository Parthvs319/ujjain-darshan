package auth;

import helpers.interfaces.SubRouterProtocol;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;

public enum AuthRouter implements SubRouterProtocol {

    INSTANCE;

    @Override
    public Router router(Vertx vertx) {
        Router router = Router.router(vertx);
        router.get("/getToken").handler(GenerateBearerTokenController.INSTANCE::handle);

//        router.get("/").handler(GetAllCitiesController.INSTANCE::handle);
//        router.delete("/:id").handler(.INSTANCE::handle);
//        router.post("/removeField").handler(.INSTANCE::handle);
        return router;
    }
}
