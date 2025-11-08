package models.sql;

import io.ebean.Model;
import io.ebean.Finder;
import javax.persistence.*;

@Entity
@Table(name = "temples")
public class Temple extends Model {
    public static final Finder<Long, Temple> find = new Finder<>(Temple.class);

    @Id
    public Long id;

    @Column(nullable = false)
    public String tenantId;

    @Column(nullable = false)
    public String name;

    public String address;
    public String city;
}
