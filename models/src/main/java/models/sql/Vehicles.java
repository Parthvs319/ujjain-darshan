package models.sql;

import helpers.blueprint.models.BaseModel;
import io.ebean.annotation.DbJsonB;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.enums.Status;
import models.json.vehicles.DriverOnboardingDetails;
import models.json.vehicles.VehicleDetails;

import javax.persistence.*;
import java.sql.Driver;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "vehicles")
public class Vehicles extends BaseModel {

    @OneToOne
    private User user;

    @ManyToOne
    private Driver driver;

    @ManyToOne
    private City city;

    public VehicleDetails getDetails() {
        if(this.details == null) {
            return new VehicleDetails();
        }
        return details;
    }

    private Status status = Status.PENDING;

    @DbJsonB
    private VehicleDetails details;

}

