package models.repos;

import helpers.sql.SqlFinder;
import models.sql.User;

import java.util.List;

public enum UserRepository {
    INSTANCE;

    private SqlFinder<Long , User> finder = new SqlFinder<>(User.class);

    public User byId(Long id ) {
        return finder.query().where().eq("t0.id" , id).setMaxRows(1).findOne();
    }

    public User byMobile(String mobile) {
        return finder.query().where().eq("t0.mobile" , mobile).setMaxRows(1).findOne();
    }

    public User byEmail(String email) {
        return finder.query().where().eq("t0.email" , email).setMaxRows(1).findOne();
    }

    public List<User> finder() {
        return finder.query().findList();
    }

}
