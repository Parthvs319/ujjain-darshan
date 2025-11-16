package models.sql;

import helpers.blueprint.models.BaseModel;
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

    // Note: id field is inherited from BaseModel, no need to redefine it

    @Column(nullable = false)
    public String tenantId;

    @Column(nullable = false)
    public String name;

    public String address;
    public Integer rating;
    public String city;
}
