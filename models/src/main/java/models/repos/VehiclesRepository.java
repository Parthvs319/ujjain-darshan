package models.repos;

import helpers.sql.SqlFinder;
import io.ebean.ExpressionList;
import models.sql.Pujari;
import models.sql.Vehicles;

import java.util.List;

public enum VehiclesRepository {

    INSTANCE;

    public SqlFinder<Long, Vehicles> finder = new SqlFinder<>(Vehicles.class);

    public List<Vehicles> finder() {
        return finder.query().where().findList();
    }

    public ExpressionList<Vehicles> exprFinder() {
        return finder.query().where();
    }
}
