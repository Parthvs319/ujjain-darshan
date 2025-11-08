package src.web.handlers;

import io.vertx.ext.web.RoutingContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import src.models.sql.Trip;
import src.services.PaymentService;
import src.web.exceptions.BadRequestException;
import src.web.exceptions.NotFoundException;
import src.web.exceptions.UnauthorizedException;

import javax.validation.*;
import java.util.Map;
import java.time.LocalDate;

public class TripHandler {
    static ObjectMapper mapper = new ObjectMapper();
    static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static class CreateTripRequest {
        @NotNull(message = "userId required")
        public Long userId;
        @NotNull(message = "startDate required")
        public String startDate;
        @NotNull(message = "endDate required")
        public String endDate;
    }

    public static void createTrip(RoutingContext ctx) {
        try {
            CreateTripRequest req = mapper.readValue(ctx.getBodyAsString(), CreateTripRequest.class);
            var v = validator.validate(req);
            if (!v.isEmpty()) throw new BadRequestException(v.iterator().next().getMessage());

            // ensure authenticated user id matches request userId (basic check)
            Long authUserId = ctx.get("userId");
            if (authUserId == null || !authUserId.equals(req.userId)) {
                throw new UnauthorizedException("user_mismatch");
            }

            Trip t = new Trip();
            t.tenantId = (String) ctx.get("tenantId");
            t.userId = req.userId;
            Map body = mapper.readValue(ctx.getBodyAsString(), Map.class);
            t.cabDetails = (String) body.get("cabDetails");
            t.pickupLocation = (String) body.get("pickupLocation");
            t.dropLocation = (String) body.get("dropLocation");
            t.startDate = LocalDate.parse(req.startDate);
            t.endDate = LocalDate.parse(req.endDate);
            t.hotelIdsJson = mapper.writeValueAsString(body.getOrDefault("hotels", new String[]{}));
            t.templeIdsJson = mapper.writeValueAsString(body.getOrDefault("temples", new String[]{}));
            t.itineraryJson = mapper.writeValueAsString(body.getOrDefault("itinerary", new String[]{}));
            t.save();

            // Request payment (stub)
            Map pay = PaymentService.createPaymentForTrip(t);
            ctx.response().putHeader("Content-Type", "application/json").end(mapper.writeValueAsString(Map.of("trip", t, "payment", pay)));
        } catch (BadRequestException e) {
            ctx.fail(e);
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    public static void getTrip(RoutingContext ctx) {
        try {
            String id = ctx.pathParam("id");
            Trip t = Trip.find.byId(Long.parseLong(id));
            if (t == null) {
                throw new NotFoundException("not_found");
            }
            // ensure tenant/user access
            Long authUserId = ctx.get("userId");
            if (authUserId == null || !authUserId.equals(t.userId)) {
                throw new UnauthorizedException("forbidden");
            }
            ctx.response().putHeader("Content-Type", "application/json").end(mapper.writeValueAsString(t));
        } catch (Exception e) {
            ctx.fail(e);
        }
    }
}
