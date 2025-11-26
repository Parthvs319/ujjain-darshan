package models.repos;

import helpers.sql.SqlFinder;
import io.ebean.ExpressionList;
import models.sql.Hotel;

import java.util.List;

public enum HotelRepository {

    INSTANCE;

    public SqlFinder<Long, Hotel> finder = new SqlFinder<>(Hotel.class);

    public List<Hotel> finder() {
        return finder.query().where().findList();
    }

    public ExpressionList<Hotel> exprFinder() {
        return finder.query().where();
    }
}
