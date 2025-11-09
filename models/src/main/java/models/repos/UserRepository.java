package models.repos;

import helpers.sql.SqlFinder;
import models.sql.User;

public enum UserRepository {
    INSTANCE;

    private SqlFinder<Long , User> finder = new SqlFinder<>(User.class);

    public User byId(Long id ) {
        return finder.query().where().eq("t0.id" , id).setMaxRows(1).findOne();
    }


}
