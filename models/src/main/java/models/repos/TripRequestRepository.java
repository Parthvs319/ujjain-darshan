package models.repos;

import helpers.sql.SqlFinder;
import io.ebean.ExpressionList;
import models.sql.TripRequest;
import models.sql.Drivers;
import models.sql.Pujari;

import java.util.List;

public enum TripRequestRepository {
    INSTANCE;

    public SqlFinder<Long, TripRequest> finder = new SqlFinder<>(TripRequest.class);

    public List<TripRequest> findByTripId(Long tripId) {
        return finder.query().where().eq("trip.id", tripId).findList();
    }

    public List<TripRequest> findPendingByDriver(Drivers driver) {
        return finder.query().where()
                .eq("driver.id", driver.getId())
                .eq("status", models.enums.Status.PENDING)
                .findList();
    }

    public List<TripRequest> findPendingByPujari(Pujari pujari) {
        return finder.query().where()
                .eq("pujari.id", pujari.getId())
                .eq("status", models.enums.Status.PENDING)
                .findList();
    }

    public ExpressionList<TripRequest> exprFinder() {
        return finder.query().where();
    }
}


