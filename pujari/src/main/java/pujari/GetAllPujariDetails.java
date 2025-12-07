package pujari;

import helpers.annotations.UserAnnotation;
import helpers.customErrors.RoutingError;
import helpers.interfaces.BaseController;
import helpers.utils.ResponseUtils;
import io.vertx.rxjava.ext.web.RoutingContext;
import lombok.Data;
import models.access.middlewear.user.UserAccessMiddleware;
import models.body.UserLoginRequest;
import models.json.pujari.PujariDetails;
import models.repos.PujariRepository;
import models.sql.Pujari;
import java.util.ArrayList;

@UserAnnotation
public enum GetAllPujariDetails implements BaseController {

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
        Response response = new Response();
        try {
            Pujari pujari = PujariRepository.INSTANCE.findByUser(request.getUser());
            if(pujari == null) {
                throw new RoutingError("Basic onboarding is not completed for this user !");
            } else {
                response.setDetails(pujari.getDetails());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RoutingError(e.getMessage());
        }
        return response;
    }

    @Data
    class Response {
        private PujariDetails details;
    }
}
