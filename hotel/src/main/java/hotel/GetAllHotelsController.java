package hotel;

import helpers.annotations.UserAnnotation;
import helpers.customErrors.RoutingError;
import helpers.interfaces.BaseController;
import helpers.utils.ResponseUtils;
import io.vertx.rxjava.ext.web.RoutingContext;
import lombok.Data;
import models.access.middlewear.user.UserAccessMiddleware;
import models.body.UserLoginRequest;
import models.enums.Status;
import models.json.hotel.HotelDetails;
import models.repos.HotelRepository;
import models.sql.Hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UserAnnotation
public enum GetAllHotelsController implements BaseController {

    INSTANCE;

    @Override
    public void handle(RoutingContext event) {
        UserAccessMiddleware.INSTANCE.with(event, new ArrayList<>(), this.getClass())
                .map(this::map)
                .subscribe(
                        o -> ResponseUtils.INSTANCE.writeJsonResponse(event, o),
                        error -> ResponseUtils.INSTANCE.handleError(event, error)
                );
    }

    private Response map(UserLoginRequest request) {
        try {
            List<Hotel> hotels = HotelRepository.INSTANCE.exprFinder().eq("status" , Status.APPROVED)
                    .findList();
            Response response = new Response();
            response.setResponse(hotels);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RoutingError(e.getMessage());
        }
    }

    @Data
    class Response {
        private List<HotelDTO> hotels = new ArrayList<>();

        private void setResponse(List<models.sql.Hotel> dbHotels) {
            this.hotels = dbHotels.stream()
                    .map(HotelDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    class HotelDTO {

        private String name;
        private Double latitude;
        private Double longitude;
        private String city;
        private boolean verified = false;
        private Long verifiedByUser = 0L;
        private String verifiedBy;
        private Status status;
        private HotelDetails details;

        HotelDTO(models.sql.Hotel hotel) {
            this.name = hotel.getName();
            this.city = hotel.getCity().getName();
            this.details = hotel.getDetails();
            this.latitude = hotel.getLatitude();
            this.longitude = hotel.getLongitude();
            this.status = hotel.getStatus();
            this.verifiedBy = hotel.getVerifiedBy();
            this.verifiedByUser = hotel.getVerifiedByUser();
            this.verified = hotel.isVerified();
        }
    }
}