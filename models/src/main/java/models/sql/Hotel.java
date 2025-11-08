package models.sql;

import helpers.blueprint.BaseModel;
import io.ebean.Model;
import io.ebean.Finder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "hotels")
public class Hotel extends BaseModel {
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
