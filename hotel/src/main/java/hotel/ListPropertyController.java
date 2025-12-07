package hotel;


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
import models.json.hotel.HotelDetails;
import models.repos.CityRepository;
import models.sql.Hotel;
import models.sql.User;

import java.util.ArrayList;
import java.util.List;

@UserAnnotation
public enum ListPropertyController implements BaseController {

    INSTANCE;

    public List<RequestItem> items() {
        List<helpers.utils.RequestItem> items = new ArrayList<>();
        items.add(RequestItem.builder().key("details").required(true).itemType(RequestItemType.OBJECT).objectClass(HotelDetails.class).build());
        items.add(RequestItem.builder().required(true).itemType(RequestItemType.DOUBLE).key("lat").build());
        items.add(RequestItem.builder().required(true).itemType(RequestItemType.DOUBLE).key("lng").build());
        items.add(RequestItem.builder().required(true).itemType(RequestItemType.STRING).key("name").build());
        items.add(RequestItem.builder().required(true).itemType(RequestItemType.INTEGER).key("cityId").build());
        return items;
    }

    @Override
    public void handle(RoutingContext event) {
        UserAccessMiddleware.INSTANCE.with(event , items() ,  this.getClass())
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
            HotelDetails details = request.getRequest().get("details");
            if(details == null) {
                throw new RoutingError("Details are must !");
            }
            if(details.getImages() == null || (details.getImages().isEmpty())) {
                throw new RoutingError("Images are must !");
            }
            if(details.getImages().size() < 5) {
                throw new RoutingError("Minimum of 5 images are mandatory !");
            }
            Hotel hotel = new Hotel();
            hotel.setUser(user);
            hotel.setLatitude(request.getRequest().get("lat"));
            hotel.setLongitude(request.getRequest().get("lng"));
            hotel.setDetails(details);
            hotel.setName(request.getRequest().get("name"));
            hotel.setCity(CityRepository.INSTANCE.exprFinder().eq("id" , request.getRequest().get("cityId")).findOne());
            hotel.save();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RoutingError(e.getMessage());
        }
        return new SuccessResponse();
    }

}

