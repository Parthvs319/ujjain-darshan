package helpers.middlewear;

import helpers.utils.RequestHelper;
import helpers.utils.RequestItem;
import helpers.blueprint.enums.RequestItemType;
import helpers.utils.RequestZipped;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.ext.web.RoutingContext;

import java.util.List;

public interface BaseMiddleware {


    default RequestZipped zip(RoutingContext routingContext, List<RequestItem> items) {
        if (routingContext.request().method().equals(HttpMethod.GET) || routingContext.request().method().equals(HttpMethod.DELETE))
            return new RequestZipped(routingContext, RequestHelper.INSTANCE.mapGetRequest(routingContext, items));
        else if (routingContext.request().getHeader("Content-Type") != null && (routingContext.request().getHeader("Content-Type").toLowerCase().contains("multipart/form-data")))
            return new RequestZipped(routingContext, RequestHelper.INSTANCE.mapMultipartRequest(routingContext, items));
        else
            return new RequestZipped(routingContext, RequestHelper.INSTANCE.mapJsonRequest(routingContext.getBodyAsJson(), items));
    }


    default RequestItem convertRequestParam(String s){
        String[] split = s.split(":");
        RequestItem item = new RequestItem();
        String param = split[0];
        item.setKey(param);
        String object = split[1];
        String[] objects = object.split("@");
        String type;
        if(objects.length>1){
            type = objects[0];
            if(objects[1].equalsIgnoreCase("required")){
                item.setRequired(true);
            }else {
                item.setRequired(false);
            }
        }else {
            type = object;
            item.setRequired(true);
        }
        if(type.equalsIgnoreCase("string") || type.equalsIgnoreCase("s")){
            item.setItemType(RequestItemType.STRING);
        }else if(type.equalsIgnoreCase("integer") || type.equalsIgnoreCase("number")|| type.equalsIgnoreCase("i")){
            item.setItemType(RequestItemType.INTEGER);
        }else if(type.equalsIgnoreCase("decimal")  ||type.equalsIgnoreCase("double")  || type.equalsIgnoreCase("d")|| type.equalsIgnoreCase("db")){
            item.setItemType(RequestItemType.DOUBLE);
        }else if(type.equalsIgnoreCase("date")  || type.equalsIgnoreCase("dt")){
            item.setItemType(RequestItemType.DATE);
        }else if(type.equalsIgnoreCase("json") ){
            item.setItemType(RequestItemType.JSONOBJECT);
        }else if(type.equalsIgnoreCase("email") ){
            item.setItemType(RequestItemType.EMAIL);
        }else if(type.equalsIgnoreCase("bool") || type.equalsIgnoreCase("boolean")){
            item.setItemType(RequestItemType.BOOLEAN);
        }else if(type.equalsIgnoreCase("object")){
            item.setItemType(RequestItemType.OBJECT);
        }
        return item;
    }

}
