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

        // Register entity packages - this is critical for Ebean to find entities
        config.addPackage("models.sql");
        config.addPackage("helpers.blueprint.models");
        
        // Disable DDL generation and migration by default (enable only in dev if needed)
        // Set to false to avoid requiring ebean-ddl-generator and ebean-migration in production
        config.setDdlGenerate(false);
        config.setDdlRun(false);

        // Try to load from properties file if available (will override above settings)
        try {
            config.loadFromProperties();
        } catch (Exception e) {
            // Properties file not found or not accessible, continue with explicit config
            System.out.println("⚠️  Could not load ebean.properties, using explicit configuration");
        }

        DataSourceConfig ds = new DataSourceConfig();

        // Railway environment variables (internal)
        String host = System.getenv("MYSQLHOST");
        String port = System.getenv("MYSQLPORT");
        String db = System.getenv("MYSQLDATABASE");
        String user = System.getenv("MYSQLUSER");
        String pass = System.getenv("MYSQLPASSWORD");

        // Fallback for local dev
        if (host == null) host = "localhost";
        if (port == null) port = "3306";
        if (db == null) db = "ujjain-darshan-db";
        if (user == null) user = "root";
        if (pass == null) pass = "PaZIjGjnKVEbyKjMqthELuxgVqVLBNgk";

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&allowPublicKeyRetrieval=true";

        ds.setUrl(jdbcUrl);
        ds.setUsername(user);
        ds.setPassword(pass);
        ds.setDriver("com.mysql.cj.jdbc.Driver");

        config.setDataSourceConfig(ds);

        this.database = DatabaseFactory.create(config);
        System.out.println("✅ Connected to MySQL: " + jdbcUrl);
        System.out.println("✅ Registered entity packages: models.sql, helpers.blueprint.models");
    }

    public Database getServer() {
        return database;
    }

    public static void init() {
        SqlConfigFactory.MASTER.getServer();
    }
}