package sql;

import io.ebean.Model;
import io.ebean.Finder;
import javax.persistence.*;

@Entity
@Table(name = "hotels")
public class Hotel extends Model {
    public static final Finder<Long, Hotel> find = new Finder<>(Hotel.class);

    @Id
    public Long id;

    @Column(nullable = false)
    public String tenantId;

    @Column(nullable = false)
    public String name;

    public String address;
    public Integer rating;
    public String city;
}
