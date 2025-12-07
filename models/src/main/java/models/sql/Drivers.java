package models.sql;

import helpers.blueprint.models.BaseModel;
import io.ebean.annotation.DbJsonB;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.enums.Status;
import models.json.vehicles.DriverOnboardingDetails;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "drivers")
public class Drivers extends BaseModel {

    @OneToOne
    private User user;

    @ManyToOne
    private City city;

    public DriverOnboardingDetails getDetails() {
        if(this.details == null) {
            return new DriverOnboardingDetails();
        }
        return details;
    }

    private Status status;

    @DbJsonB
    private DriverOnboardingDetails details;

}

