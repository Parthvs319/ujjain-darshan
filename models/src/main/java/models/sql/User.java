package models.sql;

import helpers.blueprint.models.BaseModel;
import io.ebean.Model;
import io.ebean.Finder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.enums.UserType;

import javax.persistence.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "users")
public class User extends BaseModel {

    @Column(nullable = false, unique = true)
    public String mobile;

    public String name;

    public String email;

    public String password;

    public String residingCity;

    @Column(nullable = false)
    public UserType userType;

    public boolean verified = false;

}
