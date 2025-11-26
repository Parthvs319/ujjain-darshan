package hotel;


import helpers.annotations.HotelAnnotation;
import helpers.annotations.UserAnnotation;
import helpers.blueprint.enums.RequestItemType;
import helpers.customErrors.RoutingError;
import helpers.interfaces.BaseController;
import helpers.utils.RequestItem;
import helpers.utils.ResponseUtils;
import helpers.utils.SuccessResponse;
import io.vertx.rxjava.ext.web.RoutingContext;
import models.access.middlewear.user.UserAccessMiddleware;
import models.body.UserLoginRequest;
import models.enums.UserType;
import models.json.HotelDetails;
import models.repos.CityRepository;
import models.repos.HotelRepository;
import models.repos.UserRepository;
import models.sql.Hotel;
import models.sql.User;

import java.util.ArrayList;
import java.util.List;

@UserAnnotation
public enum RemoveHotelController implements BaseController {

    INSTANCE;


    @Override
    public void handle(RoutingContext event) {
        UserAccessMiddleware.INSTANCE.with(event , new ArrayList<>() ,  this.getClass())
                .map(this::map)
                .subscribe(
                        o -> ResponseUtils.INSTANCE.writeJsonResponse(event, o),
                        error -> {
                            ResponseUtils.INSTANCE.handleError(event, error);
                        }
                );
    }

    private SuccessResponse map(UserLoginRequest request) {
        try {
            if (!request.getUser().getUserType().equals(UserType.HOTEL_ADMIN)) {
                throw new RoutingError("You are not permitted to list properties !");
            }
            User user = request.getUser();
            Long hotelId = Long.parseLong(request.getRoutingContext().request().getParam("id"));
            Hotel hotel = HotelRepository.INSTANCE.exprFinder()
                    .eq("id" , hotelId)
                    .findOne();
            if(hotel != null) {
                if(hotel.getUser().getId().equals(user.getId())) {
                    hotel.getDetails().setDeactivatedBy(user.getUserType());
                    hotel.getDetails().setDeactivatedById(user.getId());
                    hotel.delete();
                } else {
                    throw new RoutingError("Hotel not registered under this user !");
                }
            } else {
                throw new RoutingError("Invalid id passed !");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RoutingError(e.getMessage());
        }
        return new SuccessResponse();
    }

}


