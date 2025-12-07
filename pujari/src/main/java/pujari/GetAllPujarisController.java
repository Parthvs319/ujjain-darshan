package pujari;

import helpers.annotations.UserAnnotation;
import helpers.customErrors.RoutingError;
import helpers.interfaces.BaseController;
import helpers.utils.ResponseUtils;
import io.vertx.rxjava.ext.web.RoutingContext;
import lombok.Data;
import models.access.middlewear.user.UserAccessMiddleware;
import models.body.UserLoginRequest;
import models.enums.Status;
import models.json.pujari.PujariDetails;
import models.repos.PujariRepository;
import models.sql.Pujari;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@UserAnnotation
public enum GetAllPujarisController implements BaseController {

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
            List<Pujari> hotels = PujariRepository.INSTANCE.exprFinder().eq("status" , Status.APPROVED)
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
        private List<PujariDTO> pujaris = new ArrayList<>();

        private void setResponse(List<models.sql.Pujari> pujaris) {
            this.pujaris = pujaris.stream()
                    .map(PujariDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    class PujariDTO {
        private Long id;
        private String name;
        private String city;
        private PujariDetails details;

        PujariDTO(models.sql.Pujari pujari) {
            this.id = pujari.getId();
            this.name = pujari.getUser().getName();
            this.city = pujari.getCity().getName();
            this.details = pujari.getDetails();
        }
    }
}