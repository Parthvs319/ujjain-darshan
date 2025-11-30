package models.sql;

import helpers.blueprint.models.BaseModel;
import io.ebean.annotation.DbJsonB;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.json.HotelDetails;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "hotels")
public class Hotel extends BaseModel {

    @ManyToOne
    private User user;

    private String name;

    private Double latitude;

    private Double longitude;

    @ManyToOne
    private City city;

    public HotelDetails getDetails() {
        if(this.details == null) {
            return new HotelDetails();
        }
        return details;
    }

    @DbJsonB
    private HotelDetails details;

    private boolean verifiedByAdmin = false;

}
