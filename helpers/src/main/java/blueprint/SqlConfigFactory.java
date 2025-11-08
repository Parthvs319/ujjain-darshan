package blueprint;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;

/**
 * Ebean Database Factory — only Master DB configured.
 */
public enum SqlConfigFactory {
    MASTER;

    private final Database database;

    SqlConfigFactory() {
        DatabaseConfig config = new DatabaseConfig();
        config.setName("master");
        config.loadFromProperties();
        config.setRegister(true);
        config.setDefaultServer(true);
        this.database = DatabaseFactory.create(config);
    }

    public Database getServer() {
        return database;
    }
    public static void init() {
        SqlConfigFactory.MASTER.getServer();
    }
}
