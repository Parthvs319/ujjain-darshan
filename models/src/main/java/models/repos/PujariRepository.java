package models.repos;

import helpers.sql.SqlFinder;
import io.ebean.ExpressionList;
import models.sql.Pujari;
import models.sql.User;

import java.util.List;

public enum PujariRepository {

    INSTANCE;

    public SqlFinder<Long, Pujari> finder = new SqlFinder<>(Pujari.class);

    public List<Pujari> finder() {
        return finder.query().where().findList();
    }

    public Pujari findByUser(User user) {
        return finder.query().where().eq("user.id" , user.getId()).setMaxRows(1).findOne();
    }

    public ExpressionList<Pujari> exprFinder() {
        return finder.query().where();
    }
}

