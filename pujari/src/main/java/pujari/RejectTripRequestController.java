package pujari;

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
import models.enums.RequestType;
import models.enums.Status;
import models.enums.UserType;
import models.repos.PujariRepository;
import models.repos.TripRequestRepository;
import models.sql.Pujari;
import models.sql.TripRequest;

import java.util.ArrayList;
import java.util.List;

@UserAnnotation
public enum RejectTripRequestController implements BaseController {

    INSTANCE;

    @Override
    public void handle(RoutingContext event) {
        UserAccessMiddleware.INSTANCE.with(event, items(), this.getClass())
                .map(this::map)
                .subscribe(
                        o -> ResponseUtils.INSTANCE.writeJsonResponse(event, o),
                        error -> ResponseUtils.INSTANCE.handleError(event, error)
                );
    }

    private SuccessResponse map(UserLoginRequest request) {
        if (!request.getUser().getUserType().equals(UserType.PUJARI)) {
            throw new RoutingError("You are not permitted to reject trip requests!");
        }

        Long requestId = request.getRequest().get("requestId");
        if (requestId == null) {
            throw new RoutingError("Request ID is required!");
        }

        String rejectionReason = request.getRequest().get("rejectionReason");

        // Find the pujari for this user
        Pujari pujari = PujariRepository.INSTANCE.exprFinder()
                .eq("user.id", request.getUser().getId())
                .findOne();
        
        if (pujari == null) {
            throw new RoutingError("Pujari profile not found!");
        }

        // Find the trip request
        TripRequest tripRequest = TripRequestRepository.INSTANCE.exprFinder()
                .eq("id", requestId)
                .eq("pujari.id", pujari.getId())
                .eq("requestType", RequestType.PUJARI)
                .findOne();

        if (tripRequest == null) {
            throw new RoutingError("Trip request not found or you don't have permission to reject it!");
        }

        if (tripRequest.getStatus() != Status.PENDING) {
            throw new RoutingError("This trip request has already been " + tripRequest.getStatus().getValue().toLowerCase() + "!");
        }

        // Reject the request
        tripRequest.setStatus(Status.REJECTED);
        tripRequest.setRejectionReason(rejectionReason != null ? rejectionReason : "Pujari rejected the request");
        tripRequest.update();

        return new SuccessResponse(true, "Trip request rejected successfully");
    }

    public List<RequestItem> items() {
        List<RequestItem> items = new ArrayList<>();
        items.add(RequestItem.builder().key("requestId").itemType(RequestItemType.INTEGER).required(true).build());
        items.add(RequestItem.builder().key("rejectionReason").itemType(RequestItemType.STRING).required(false).build());
        return items;
    }
}

