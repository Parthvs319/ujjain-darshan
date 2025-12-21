package models.repos;

import helpers.sql.SqlFinder;
import io.ebean.ExpressionList;
import models.sql.Trip;
import models.sql.User;

import java.util.List;

public enum TripsRepository {

    INSTANCE;

    public SqlFinder<Long, Trip> finder = new SqlFinder<>(Trip.class);

    public List<Trip> finder() {
        return finder.query().where().findList();
    }

    public Trip findByUser(User user) {
        return finder.query().where().eq("user.id" , user.getId()).setMaxRows(1).findOne();
    }

    public List<Trip> findByUserId(Long userId) {
        return finder.query().where().eq("user.id", userId).findList();
    }

    public ExpressionList<Trip> exprFinder() {
        return finder.query().where();
    }
}
