package models.repos;

import helpers.sql.SqlFinder;
import io.ebean.ExpressionList;
import models.sql.Drivers;
import models.sql.Pujari;

import java.util.List;

public enum DriverRepository {

    INSTANCE;

    public SqlFinder<Long, Drivers> finder = new SqlFinder<>(Drivers.class);

    public List<Drivers> finder() {
        return finder.query().where().findList();
    }

    public ExpressionList<Drivers> exprFinder() {
        return finder.query().where();
    }
}
