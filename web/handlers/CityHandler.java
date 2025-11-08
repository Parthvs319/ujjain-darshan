package src.web.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CityHandler {
    static ObjectMapper mapper = new ObjectMapper();

//    public static void list(RoutingContext ctx) {
//        List<City> list = City.find.all();
//        try {
//            ctx.response().putHeader("Content-Type", "application/json").end(mapper.writeValueAsString(list));
//        } catch (Exception e) {
//            ctx.response().setStatusCode(500).end("{"error":"internal"}");
//        }
//    }
}
