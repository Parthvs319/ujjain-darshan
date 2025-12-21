package models.sql;

import helpers.blueprint.models.BaseModel;
import io.ebean.annotation.DbJsonB;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.enums.RequestType;
import models.enums.Status;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "trip_requests")
public class TripRequest extends BaseModel {
    
    @ManyToOne
    private Trip trip;
    
    @ManyToOne
    private Drivers driver;
    
    @ManyToOne
    private Pujari pujari;
    
    @ManyToOne
    private Vehicles vehicle;
    
    @Enumerated(EnumType.STRING)
    private RequestType requestType;
    
    private Status status = Status.PENDING;
    
    private String rejectionReason;
    
    private String templeName;

    @DbJsonB
    private List<String> pujaNames = new ArrayList<>(); // List of puja names for this temple
}


