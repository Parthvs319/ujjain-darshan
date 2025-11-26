package hotel;


import helpers.annotations.UserAnnotation;
import helpers.customErrors.RoutingError;
import helpers.interfaces.BaseController;
import helpers.interfaces.SubRouterProtocol;
import helpers.utils.ResponseUtils;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import lombok.Data;
import models.access.middlewear.user.UserAccessMiddleware;
import models.body.UserLoginRequest;
import models.enums.UserType;
import models.json.HotelDetails;
import models.repos.HotelRepository;
import models.sql.Hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum HotelRouter implements SubRouterProtocol {

    INSTANCE;

    @Override
    public Router router(Vertx vertx) {
        Router router = Router.router(vertx);
        router.get("/getAllHotels").handler(GetAllHotelsController.INSTANCE::handle);
        router.get("/:id/getAllUsers").handler(GetAllHotelsByUserController.INSTANCE::handle);
        router.post("/add").handler(ListPropertyController.INSTANCE::handle);
        router.delete("/:id/remove").handler(RemoveHotelController.INSTANCE::handle);
//        router.post("/update").handler(UpdateHotelController.INSTANCE::handle);
        router.post("/activate").handler(ActivateHotelController.INSTANCE::handle);


        return router;
    }
}

