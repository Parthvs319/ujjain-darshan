package driver;

import helpers.annotations.UserAnnotation;
import helpers.customErrors.RoutingError;
import helpers.interfaces.BaseController;
import helpers.utils.ResponseUtils;
import io.vertx.rxjava.ext.web.RoutingContext;
import lombok.Data;
import models.access.middlewear.user.UserAccessMiddleware;
import models.body.UserLoginRequest;
import models.json.hotel.HotelDetails;
import models.repos.HotelRepository;
import models.repos.VehiclesRepository;
import models.sql.Hotel;
import models.sql.Vehicles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UserAnnotation
public enum GetAllVehiclesController implements BaseController {

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
            List<Vehicles> hotels = VehiclesRepository.INSTANCE.exprFinder().eq("")
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
        private Long id;
        private Double latitude;
        private Double longitude;
        private String city;
        private HotelDetails details;

        HotelDTO(models.sql.Hotel hotel) {
            this.name = hotel.getName();
            this.id = hotel.getId();
            this.city = hotel.getCity().getName();
            this.details = hotel.getDetails();
            this.latitude = hotel.getLatitude();
            this.longitude = hotel.getLongitude();
        }
    }
}
