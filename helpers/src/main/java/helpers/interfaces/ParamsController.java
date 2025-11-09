package helpers.interfaces;

import helpers.utils.RequestHelper;
import helpers.utils.RequestItem;
import helpers.utils.RequestZipped;
import io.vertx.rxjava.ext.web.RoutingContext;

import java.util.List;


public interface ParamsController extends BaseController {


    List<RequestItem> items();

    default RequestZipped map(RoutingContext event){
        return RequestHelper.INSTANCE.requestZipped(event,items());
    }
}
