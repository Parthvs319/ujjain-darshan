package models.repos;


import helpers.blueprint.SqlFinder;
import models.sql.City;

import java.util.List;

public enum CityRepository {

    INSTANCE;

    public SqlFinder<Long, City> citySqlFinder = new SqlFinder<>(City.class);

    public List<City> finder() {
        return citySqlFinder.query().where().findList();
    }

}
