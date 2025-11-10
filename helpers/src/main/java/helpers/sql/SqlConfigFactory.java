package helpers.sql;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;

/**
 * Ebean Database Factory — works locally & on Render using env vars.
 */
public enum SqlConfigFactory {
    MASTER;

    private final Database database;

    SqlConfigFactory() {
        DatabaseConfig config = new DatabaseConfig();
        config.setName("master");
        config.setRegister(true);
        config.setDefaultServer(true);

        DataSourceConfig ds = new DataSourceConfig();

        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");

        if (url == null) {
            url = "jdbc:postgresql://dpg-d491agp5pdvs73cls160-a.oregon-postgres.render.com/ujjain_darshan_db";
        }
        if (user == null) {
            user = "ujjain_darshan_db_user";
        }
        if (pass == null) {
            pass = "kWbYpwS5yFfRcFKjG5sI0yIW3DCO4HIV";
        }

        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        ds.setDriver("org.postgresql.Driver");

        config.setDataSourceConfig(ds);

        this.database = DatabaseFactory.create(config);
        System.out.println("✅ PostgreSQL initialized successfully (" + url + ")");
    }

    public Database getServer() {
        return database;
    }

    public static void init() {
        SqlConfigFactory.MASTER.getServer();
    }
}