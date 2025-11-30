package hotel;

import helpers.annotations.UserAnnotation;
import helpers.customErrors.RoutingError;
import helpers.interfaces.BaseController;
import helpers.utils.ResponseUtils;
import helpers.utils.SuccessResponse;
import io.vertx.rxjava.ext.web.RoutingContext;
import models.access.middlewear.user.UserAccessMiddleware;
import models.body.UserLoginRequest;
import models.enums.UserType;
import models.repos.HotelRepository;
import models.sql.Hotel;;
import java.util.ArrayList;

@UserAnnotation
public enum VerifyHotelDetailsController implements BaseController {

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
            if (!request.getUser().getUserType().equals(UserType.ADMIN)) {
                throw new RoutingError("You are not allowed to verify hotels !");
            }
            Long hotelId = Long.parseLong(request.getRoutingContext().request().getParam("id"));
            Hotel hotel = HotelRepository.INSTANCE.exprFinder()
                    .eq("id" , hotelId)
                    .findOne();
            if(hotel != null) {
                hotel.setVerified(true);
                hotel.setVerifiedByUser(request.getUser().getId());
                hotel.setVerifiedBy(request.getUser().getName());
                hotel.update();
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



