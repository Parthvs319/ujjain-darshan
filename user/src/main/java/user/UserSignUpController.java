package user;

import helpers.annotations.UserAnnotation;
import helpers.blueprint.enums.RequestItemType;
import helpers.interfaces.BaseController;
import helpers.utils.RequestItem;
import helpers.utils.ResponseUtils;
import helpers.utils.SuccessResponse;
import io.vertx.rxjava.ext.web.RoutingContext;
import models.access.middlewear.UserAccessMiddleware;
import models.body.UserLoginRequest;
import models.repos.UserRepository;
import models.sql.User;
import models.enums.UserType;
import org.mindrot.jbcrypt.BCrypt;
import models.access.tokens.BearerToken;
import models.access.tokens.TokenService;

import java.util.ArrayList;
import java.util.List;

@UserAnnotation
public enum UserSignUpController implements BaseController {

    INSTANCE;

    public List<RequestItem> items() {
        List<RequestItem> items = new ArrayList<>();

        items.add(RequestItem.builder().key("mobile").itemType(RequestItemType.STRING).required(true).build());
        items.add(RequestItem.builder().key("password").itemType(RequestItemType.STRING).required(true).build());
        items.add(RequestItem.builder().key("name").itemType(RequestItemType.STRING).required(true).build());
        items.add(RequestItem.builder().key("email").itemType(RequestItemType.STRING).required(true).build());
        items.add(RequestItem.builder().key("residingCity").itemType(RequestItemType.STRING).required(false).build());

        return items;
    }

    @Override
    public void handle(RoutingContext event) {

        UserAccessMiddleware.INSTANCE.with(event, items(), this)
                .map(this::map)
                .subscribe(
                        response -> ResponseUtils.INSTANCE.writeJsonResponse(event, response),
                        error -> ResponseUtils.INSTANCE.handleError(event, error)
                );
    }

    private SuccessResponse map(UserLoginRequest request) {
        String mobile = request.getRequest().get("mobile");
        String password = request.getRequest().get("password");
        String name = request.getRequest().get("name");
        String email = request.getRequest().get("email");
        String residingCity = request.getRequest().get("residingCity");

        if (mobile == null || mobile.trim().isEmpty()) {
            throw new RuntimeException("Mobile number is required");
        }
        mobile = mobile.trim();
        if (!mobile.matches("^[0-9]+$")) {
            throw new RuntimeException("Mobile number must contain digits only");
        }
        if (!mobile.matches("^[6-9][0-9]{9}$")) {
            throw new RuntimeException("Enter a valid 10-digit Indian mobile number");
        }
        if (mobile.matches("^(\\d)\\1{9}$")) {
            throw new RuntimeException("Mobile number cannot have all repeating digits");
        }
        if (password == null || password.isEmpty())
            throw new RuntimeException("Password is required");
        if (email == null || email.isEmpty())
            throw new RuntimeException("Email is required");

        User existing = UserRepository.INSTANCE.byMobile(mobile);
        if (existing != null)
            throw new RuntimeException("Mobile already registered");

        User user = new User();
        user.setMobile(mobile);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setEmail(email);
        user.setName(name);
        user.setResidingCity(residingCity);
        user.setUserType(UserType.);
        user.setVerified(false);
        user.save();
        BearerToken token = TokenService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getUserType().name(),
                user.getName()
        );
        return new SuccessResponse(true, token.toString());
    }
}