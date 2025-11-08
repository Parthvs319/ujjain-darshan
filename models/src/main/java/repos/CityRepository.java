package repos;

import src.helpers.blueprint.SqlFinder;
import src.models.sql.City;

import java.util.List;

public enum CityRepository {

    INSTANCE;

    public SqlFinder<Long, City> citySqlFinder = new SqlFinder<>(City.class);

    public List<City> finder() {
        return citySqlFinder.query().where().findList();
    }

}
