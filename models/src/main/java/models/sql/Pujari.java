package models.sql;

import helpers.blueprint.models.BaseModel;
import io.ebean.annotation.DbJsonB;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.enums.Status;
import models.json.hotel.HotelDetails;
import models.json.pujari.PujariDetails;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "pujari")
public class Pujari extends BaseModel {

    @ManyToOne
    private User user;

    @ManyToOne
    private City city;

    public PujariDetails getDetails() {
        if(this.details == null) {
            return new PujariDetails();
        }
        return details;
    }

    private Status status = Status.PENDING;

    @DbJsonB
    private PujariDetails details;

}
