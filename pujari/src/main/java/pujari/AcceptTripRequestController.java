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
public enum AcceptTripRequestController implements BaseController {

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
            throw new RoutingError("You are not permitted to accept trip requests!");
        }

        Long requestId = request.getRequest().get("requestId");
        if (requestId == null) {
            throw new RoutingError("Request ID is required!");
        }

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
            throw new RoutingError("Trip request not found or you don't have permission to accept it!");
        }

        if (tripRequest.getStatus() != Status.PENDING) {
            throw new RoutingError("This trip request has already been " + tripRequest.getStatus().getValue().toLowerCase() + "!");
        }

        // Check if trip already has an assigned pujari for this temple
        // One pujari per temple - first to accept wins
        if (tripRequest.getTempleName() != null) {
            // Check if there's already an accepted request for this temple
            List<TripRequest> existingAccepted = TripRequestRepository.INSTANCE.exprFinder()
                    .eq("trip.id", tripRequest.getTrip().getId())
                    .eq("requestType", RequestType.PUJARI)
                    .eq("templeName", tripRequest.getTempleName())
                    .eq("status", models.enums.Status.APPROVED)
                    .findList();
            
            if (!existingAccepted.isEmpty()) {
                // Already accepted by another pujari for this temple
                tripRequest.setStatus(Status.REJECTED);
                tripRequest.setRejectionReason("Another pujari has already accepted this temple request");
                tripRequest.update();
                throw new RoutingError("This temple request has already been assigned to another pujari!");
            }
        }

        // Accept the request - first to accept wins! (Using APPROVED status)
        tripRequest.setStatus(Status.APPROVED);
        tripRequest.update();

        // Assign pujari to the trip (if not already assigned, or if this is the first pujari)
        if (tripRequest.getTrip().getAssignedPujari() == null) {
            tripRequest.getTrip().setAssignedPujari(pujari);
            tripRequest.getTrip().update();
        }

        // Reject all other pending requests for this same temple
        List<TripRequest> otherRequests = TripRequestRepository.INSTANCE.exprFinder()
                .eq("trip.id", tripRequest.getTrip().getId())
                .eq("requestType", RequestType.PUJARI)
                .eq("templeName", tripRequest.getTempleName())
                .eq("status", models.enums.Status.PENDING)
                .ne("id", tripRequest.getId())
                .findList();

        for (TripRequest otherRequest : otherRequests) {
            otherRequest.setStatus(Status.REJECTED);
            otherRequest.setRejectionReason("Another pujari accepted this request first");
            otherRequest.update();
        }

        // Send WhatsApp notifications to pujari and tourist after acceptance
//        WhatsAppService.sendPujariAcceptanceNotifications(tripRequest);

        String pujasList = tripRequest.getPujaNames() != null && !tripRequest.getPujaNames().isEmpty()
                ? String.join(", ", tripRequest.getPujaNames())
                : "all requested pujas";
        return new SuccessResponse(true, "Trip request accepted successfully! You have been assigned to trip: " + tripRequest.getTrip().getTripId() + " for " + tripRequest.getTempleName() + " (" + pujasList + ")");
    }

    public List<RequestItem> items() {
        List<RequestItem> items = new ArrayList<>();
        items.add(RequestItem.builder().key("requestId").itemType(RequestItemType.INTEGER).required(true).build());
        return items;
    }
}

