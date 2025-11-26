package models.access.middlewear.user;

import helpers.annotations.UserAnnotation;
import helpers.customErrors.RoutingError;
import helpers.utils.RequestItem;
import helpers.utils.RequestZipped;
import io.vertx.rxjava.ext.web.RoutingContext;
import models.access.middlewear.BaseMiddleware;
import models.body.UserLoginRequest;
import rx.Single;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public enum UserAccessMiddleware implements BaseMiddleware {
    INSTANCE;

    public Single<UserLoginRequest> with(
            RoutingContext rc,
            List<RequestItem> items , Object clz
    ) {
        UserAnnotation role = null;
        Class<?> targetClass = (clz instanceof Class) ? (Class<?>) clz : clz.getClass();
        for (Annotation annotation : targetClass.getAnnotations()) {
            if(annotation instanceof UserAnnotation){
                role = (UserAnnotation) annotation;
            }
        }
        if(role==null){
            throw new RoutingError(409,"Invalid api");
        }
        return UserLoginMiddleware.INSTANCE.authenticationObservable(rc)
                .map(context -> {
                    List<RequestItem> cloned = new ArrayList<>(items);
                    RequestZipped zipped = zip(context, cloned);
                    UserLoginRequest req = new UserLoginRequest();
                    req.setRoutingContext(context);
                    req.setRequest(zipped.getRequest());
                    req.setUser(context.get("user"));
                    req.setIp(context.request().remoteAddress().host());
                    req.setUserAgent(context.request().getHeader("User-Agent"));
                    req.setReferer(context.request().getHeader("Referer"));
                    req.setHost(context.request().host());
                    return req;
                });
    }
}