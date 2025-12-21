package models.sql;

import helpers.blueprint.models.AttrsModel;
import io.ebean.annotation.DbJsonB;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.enums.Status;
import models.json.tourist.TripConfig;
import models.json.tourist.TripItinerary;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "trips")
public class Trip extends AttrsModel {

    public User getUser() {
        if(user!=null && user.getId()==0L)
            user = null;
        return user;
    }

    private String title;

    private String tripId;

    @ManyToOne
    private User user;
    
    @ManyToOne
    private City city;

    private Timestamp startDate;

    private Timestamp endDate;
    
    @ManyToOne
    private Drivers assignedDriver;
    
    @ManyToOne
    private Pujari assignedPujari;
    
    private Integer numberOfPassengers;

    public void setEndDate(Timestamp endDate) {
        this.endDate = new Timestamp(endDate.getTime());
    }

    @DbJsonB
    private HashMap<String,String> content;

    public HashMap<String,String> getContent(){
        if(content==null){
            content = new HashMap<>();
        }
        return content;
    }

    @DbJsonB
    private List<TripItinerary> itineraries  = new ArrayList<>();


    private Long budget = 0L;


    public Long getBudget() {
        return budget;
    }

    private Long used = 0L;

    public void setUsed(Long used) {
        this.used = used;
    }

    private Status status = Status.PENDING;

    @DbJsonB
    private TripConfig config;

    public TripConfig getConfig() {
        if (config == null) {
            config = new TripConfig();
        }
        return config;
    }
}
