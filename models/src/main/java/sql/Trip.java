package sql;

import io.ebean.Model;
import io.ebean.Finder;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "trips")
public class Trip extends Model {
    public static final Finder<Long, Trip> find = new Finder<>(Trip.class);

    @Id
    public Long id;

    @Column(nullable = false)
    public String tenantId;

    @Column(nullable = false)
    public Long userId; // tourist who created trip

    public String cabDetails; // e.g. cabId or description
    public String pickupLocation;
    public String dropLocation;

    public LocalDate startDate;
    public LocalDate endDate;

    // For simplicity store lists as JSON strings or separate join tables in future
    @Lob
    public String hotelIdsJson;
    @Lob
    public String templeIdsJson;
    @Lob
    public String itineraryJson; // array of intermediate locations and schedule

    public boolean paid = false;
}
