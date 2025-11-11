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

        // Try fetching the Railway DATABASE_URL
        String railwayUrl = System.getenv("DATABASE_URL");
        String jdbcUrl = null, user = null, pass = null;

        if (railwayUrl != null && railwayUrl.startsWith("mysql://")) {
            try {
                // Example: mysql://user:pass@host:port/dbname
                railwayUrl = railwayUrl.substring(8); // remove "mysql://"
                String[] userInfoSplit = railwayUrl.split("@");
                String[] creds = userInfoSplit[0].split(":");
                String[] hostInfo = userInfoSplit[1].split("/");
                String[] hostPort = hostInfo[0].split(":");

                user = creds[0];
                pass = creds[1];
                jdbcUrl = "jdbc:mysql://" + hostPort[0] + ":" + hostPort[1] + "/" + hostInfo[1];
            } catch (Exception e) {
                System.err.println("⚠️ Failed to parse DATABASE_URL: " + e.getMessage());
            }
        }

        // Fallbacks (for local development)
        if (jdbcUrl == null) {
            jdbcUrl = "jdbc:mysql://localhost:3306/ujjain-darshan-db";
        }
        if (user == null) {
            user = "root";
        }
        if (pass == null) {
            pass = "PaZIjGjnKVEbyKjMqthELuxgVqVLBNgk";
        }

        ds.setUrl(jdbcUrl);
        ds.setUsername(user);
        ds.setPassword(pass);
        ds.setDriver("com.mysql.cj.jdbc.Driver");

        config.setDataSourceConfig(ds);
        this.database = DatabaseFactory.create(config);

        System.out.println("✅ MySQL initialized successfully with URL: " + jdbcUrl);
    }

    public Database getServer() {
        return database;
    }

    public static void init() {
        SqlConfigFactory.MASTER.getServer();
    }
}