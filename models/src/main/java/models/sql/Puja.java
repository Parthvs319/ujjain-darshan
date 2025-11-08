package models.sql;

import io.ebean.Model;
import io.ebean.Finder;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pujas")
public class Puja extends Model {
    public static final Finder<Long, Puja> find = new Finder<>(Puja.class);

    @Id
    public Long id;

    @Column(nullable = false)
    public String tenantId;

    @Column(nullable = false)
    public Long templeId; // join to temple

    public String name;
    public BigDecimal price;
    public String description;
}
