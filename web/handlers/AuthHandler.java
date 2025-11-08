package src.web.handlers;

import io.vertx.ext.web.RoutingContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import src.models.sql.User;
import src.services.OtpService;
import src.services.JwtService;
import src.web.exceptions.BadRequestException;
import src.web.exceptions.UnauthorizedException;

import javax.validation.*;
import java.util.*;

public class AuthHandler {

    static ObjectMapper mapper = new ObjectMapper();
    static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static class SignupRequest {
        @NotNull(message = "mobile required")
        public String mobile;
        public String name;
        @javax.validation.constraints.Email(message = "invalid email")
        public String email;
        public String residingCity;
        public String userType;
        public String tenantId;
    }

    public static void signUp(RoutingContext ctx) {
        try {
            SignupRequest req = mapper.readValue(ctx.getBodyAsString(), SignupRequest.class);
            Set<ConstraintViolation<SignupRequest>> violations = validator.validate(req);
            if (!violations.isEmpty()) {
                throw new BadRequestException(violations.iterator().next().getMessage());
            }
            String mobile = req.mobile;
            String name = req.name;
            String email = req.email;
            String residingCity = req.residingCity;
            String userType = req.userType == null ? "TOURIST" : req.userType;
            String tenantId = req.tenantId == null ? "default" : req.tenantId;

            // create or find user
            User user = User.find.query().where().eq("mobile", mobile).findOne();
            if (user == null) {
                user = new User();
                user.mobile = mobile;
                user.name = name;
                user.email = email;
                user.residingCity = residingCity;
                user.userType = userType;
                user.tenantId = tenantId;
                user.save();
            }

            // send OTP (stub)
            String otp = OtpService.sendOtp(mobile);
            Map resp = new HashMap();
            resp.put("message", "OTP sent");
            resp.put("mobile", mobile);
            ctx.response().putHeader("Content-Type", "application/json").end(mapper.writeValueAsString(resp));
        } catch (BadRequestException e) {
            ctx.fail(e);
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    public static class VerifyRequest {
        @NotNull(message = "mobile required")
        public String mobile;
        @NotNull(message = "otp required")
        public String otp;
    }

    public static void verifyOtp(RoutingContext ctx) {
        try {
            VerifyRequest req = mapper.readValue(ctx.getBodyAsString(), VerifyRequest.class);
            Set<ConstraintViolation<VerifyRequest>> violations = validator.validate(req);
            if (!violations.isEmpty()) {
                throw new BadRequestException(violations.iterator().next().getMessage());
            }
            boolean ok = OtpService.verifyOtp(req.mobile, req.otp);
            if (!ok) {
                throw new BadRequestException("invalid_otp");
            }
            User user = User.find.query().where().eq("mobile", req.mobile).findOne();
            if (user == null) {
                throw new BadRequestException("user_not_found");
            }
            user.verified = true;
            user.save();
            ctx.response().putHeader("Content-Type", "application/json").end(mapper.writeValueAsString(Map.of("message","verified")));
        } catch (BadRequestException e) {
            ctx.fail(e);
        } catch (Exception e) {
            ctx.fail(e);
        }
    }

    public static class LoginRequest {
        @NotNull(message = "mobile required")
        public String mobile;
    }

    public static void login(RoutingContext ctx) {
        try {
            LoginRequest req = mapper.readValue(ctx.getBodyAsString(), LoginRequest.class);
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(req);
            if (!violations.isEmpty()) {
                throw new BadRequestException(violations.iterator().next().getMessage());
            }
            User user = User.find.query().where().eq("mobile", req.mobile).findOne();
            if (user == null || !user.verified) {
                throw new UnauthorizedException("not_verified_or_not_found");
            }
            String token = JwtService.issueToken(user.id, user.userType, user.tenantId);
            ctx.response().putHeader("Content-Type", "application/json").end(mapper.writeValueAsString(Map.of("token", token, "user", user)));
        } catch (BadRequestException e) {
            ctx.fail(e);
        } catch (Exception e) {
            ctx.fail(e);
        }
    }
}
