package blueprint;

import io.ebean.Database;
import io.ebean.ExpressionList;
import io.ebean.Query;
import io.ebean.UpdateQuery;
import blueprint.SqlConfigFactory;

import java.util.List;

/**
 * Generic SQL Finder for Ebean ORM.
 * Simplified — no tenant filtering (works across all users).
 */
public class SqlFinder<I, T> {

    private final Class<T> type;

    public SqlFinder(Class<T> type) {
        this.type = type;
    }

    private Database masterDb() {
        return SqlConfigFactory.MASTER.getServer();
    }

    // Fetch entity by ID
    public T byId(I id) {
        return masterDb().find(type, id);
    }

    // Fetch all records
    public List<T> all() {
        return query().findList();
    }

    // Create a base query
    public Query<T> query() {
        return masterDb().find(type);
    }

    // Create an update query
    public UpdateQuery<T> update() {
        return masterDb().update(type);
    }

    // Create a custom where clause
    public ExpressionList<T> where() {
        return masterDb().find(type).where();
    }
}