package user;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import src.helpers.interfaces.SubRouterProtocol;

public enum UserRouter implements SubRouterProtocol {

    INSTANCE;

    @Override
    public Router router(Vertx vertx) {
        Router router = Router.router(vertx);
//        router.post("/create").handler(CreateUserController.INSTANCE::handle);

//        router.get("/").handler(GetAllCitiesController.INSTANCE::handle);
//        router.delete("/:id").handler(.INSTANCE::handle);
//        router.post("/removeField").handler(.INSTANCE::handle);
        return router;
    }

}
