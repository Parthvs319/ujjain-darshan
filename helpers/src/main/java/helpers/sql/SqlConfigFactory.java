package helpers.sql;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;


public enum SqlConfigFactory {
    MASTER;

    private final Database database;

    SqlConfigFactory() {
        DatabaseConfig config = new DatabaseConfig();
        config.setName("master");
        config.setRegister(true);
        config.setDefaultServer(true);

        DataSourceConfig ds = new DataSourceConfig();

        // Fetch from Railway Environment Variables
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");

        // Fallback to your Railway MySQL credentials if not set
        if (url == null) {
            url = "jdbc:mysql://mysql.railway.internal:3306/ujjain-darshan-db";
        }
        if (user == null) {
            user = "root";
        }
        if (pass == null) {
            pass = "PaZIjGjnKVEbyKjMqthELuxgVqVLBNgk";
        }

        // ✅ Use MySQL driver
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        ds.setDriver("com.mysql.cj.jdbc.Driver");

        config.setDataSourceConfig(ds);

        this.database = DatabaseFactory.create(config);
        System.out.println("✅ MySQL initialized successfully (" + url + ")");
    }

    public Database getServer() {
        return database;
    }

    public static void init() {
        SqlConfigFactory.MASTER.getServer();
    }
}