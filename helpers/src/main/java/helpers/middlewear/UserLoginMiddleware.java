package helpers.middlewear;

import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.ext.web.RoutingContext;
import rx.Single;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.ext.web.RoutingContext;
import models.helpers.TokenService;
import models.repos.CompanyLoginRepository;
import models.repos.CompanyRepository;
import models.sql.Company;
import models.sql.CompanyLogins;
import rx.Single;
import utils.customErrors.RoutingError;
import utils.utils.RequestEvent;

import java.util.concurrent.CompletableFuture;

public enum UserLoginMiddleware {

    INSTANCE;

    public Single<RoutingContext> authenticationObservable(RoutingContext rc){
        return Single.just(rc).subscribeOn(RxHelper.blockingScheduler(rc.vertx())).map(this::authenticateRequest);
    }

    public CompanyLogins fromToken(String accessToken)
    {
        DecodedJWT jwt = TokenService.INSTANCE.decodedJWT(accessToken);
        Claim tokentype = jwt.getClaim("type");
        if (tokentype == null || !tokentype.as(String.class).equals("access_token")) {
            throw new RoutingError(409, RequestEvent.AUTHENTICATIONFAILED,  "Token Present in request doesnt have access to following apis.");
        }
        String type = "companyLogin";
        Claim istypeerror = jwt.getClaim(type);
        if (istypeerror == null || istypeerror.isNull()) {
            throw new RoutingError(409,  RequestEvent.AUTHENTICATIONFAILED,"Access Token dont hav has role to access this.");
        }
        Long id = jwt.getClaim("id").asLong();
        CompanyLogins companyLogin = CompanyLoginRepository.INSTANCE.byId(id);
        if (companyLogin == null) {
            throw new RoutingError(409,  RequestEvent.AUTHENTICATIONFAILED, "Access Token didnt has role to access this.");
        }
        Claim claim = jwt.getClaim("claim");
        if (claim == null || !claim.asString().equals(companyLogin.getTokenSecret().toString())) {
            throw new RoutingError(401,   RequestEvent.AUTHENTICATIONFAILED,"Access Token dont has role to access this.");
        }
        Company company = CompanyRepository.INSTANCE.company(companyLogin);
        if(!CompanyRepository.INSTANCE.isLive(company)){
            throw new RoutingError(409,"Company is not live anymore");
        }
        try {
            companyLogin.getAttrs().put("last_activity", String.valueOf(System.currentTimeMillis()));
            companyLogin.update();
        } catch (Exception ignored) {}
        return companyLogin;
    }


    public CompanyLogins companyLogins(RoutingContext rc){
        return  rc.get("companyLogin");
    }

    public RoutingContext authenticateRequest(RoutingContext routingContext) {
        String token = routingContext.request().getHeader("Authorization");
        if (token == null)
            throw new RoutingError(401,  RequestEvent.AUTHENTICATIONFAILED,"Token not found in the headers");
        if (token.length() < 7) {
            throw new RoutingError(401,  RequestEvent.AUTHENTICATIONFAILED,"Invalid token present");
        }
        String tokenType = token.substring(0, 6);
        if (!tokenType.equalsIgnoreCase("Bearer")) {
            throw new RoutingError(401,  RequestEvent.AUTHENTICATIONFAILED,"Invalid token type");
        }
        String accessToken = token.substring(7);
        CompanyLogins companyLogins = fromToken(accessToken);
        routingContext.put("companyLogin",companyLogins);
        Company company = CompanyRepository.INSTANCE.company(companyLogins);
        if (company != null && company.getConfig() != null && company.getConfig().isShutDownCompleteUsage()){
            throw new RoutingError(501, RequestEvent.PRECONDITIONFAILED,"System have been temporarily shutdown for usage by your organization");
        }
        return routingContext;
    }

}
