package models.sql;


import helpers.blueprint.models.BaseModel;
import io.ebean.Finder;
import io.ebean.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "refresh_token")
@Data
public class RefreshToken extends BaseModel {

    @ManyToOne
    User user;

    @Column(length = 500)
    String tokenHash;

    Date expiresAt;

    Boolean revoked = false;

    public static final Finder<Long, RefreshToken> find = new Finder<>(RefreshToken.class);

    public static RefreshToken byUserId(Long userId) {
        return find.query()
                .where()
                .eq("userId", userId)
                .eq("revoked", false)
                .setMaxRows(1)
                .findOne();
    }

    public static RefreshToken byToken(String hashedToken) {
        return find.query()
                .where()
                .eq("tokenHash", hashedToken)
                .eq("revoked", false)
                .setMaxRows(1)
                .findOne();
    }
}
