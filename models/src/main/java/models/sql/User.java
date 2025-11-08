package models.sql;

import io.ebean.Model;
import io.ebean.Finder;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User extends Model {

    public static final Finder<Long, User> find = new Finder<>(User.class);

    @Id
    private Long id;

    @Column(nullable = false)
    public String tenantId; // multi-tenant identifier

    @Column(nullable = false, unique = true)
    public String mobile;

    public String name;
    public String email;
    public String residingCity;

    @Column(nullable = false)
    public String userType; // TOURIST, PUJARI, GUIDE, DRIVER, TEMPLE_ADMIN, HOTEL_ADMIN, ADMIN

    public boolean verified = false;
    public LocalDateTime createdAt = LocalDateTime.now();

    // getters and setters omitted for brevity
}
