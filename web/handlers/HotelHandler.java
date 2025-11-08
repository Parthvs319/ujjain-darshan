package src.web.handlers;

import io.vertx.ext.web.RoutingContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import src.models.sql.Hotel;
import java.util.List;

public class HotelHandler {
    static ObjectMapper mapper = new ObjectMapper();

    public static void list(RoutingContext ctx) {
        List<Hotel> list = Hotel.find.all();
        try {
            ctx.response().putHeader("Content-Type", "application/json").end(mapper.writeValueAsString(list));
        } catch (Exception e) {
            ctx.response().setStatusCode(500).end("{"error":"internal"}");
        }
    }
}
