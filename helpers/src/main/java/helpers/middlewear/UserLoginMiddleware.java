package helpers.middlewear;

import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.ext.web.RoutingContext;
import rx.Single;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.ext.web.RoutingContext;

import java.util.concurrent.CompletableFuture;

public enum UserLoginMiddleware {

    INSTANCE;

    public Single<RoutingContext> authenticationObservable(RoutingContext rc){
        return Single.just(rc).subscribeOn(RxHelper.blockingScheduler(rc.vertx())).map(null);
    }

}
