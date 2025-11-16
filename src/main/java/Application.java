import helpers.sql.SqlConfigFactory;
import io.vertx.core.DeploymentOptions;
import io.vertx.rxjava.core.Vertx;

public class Application {
    public static void main(String[] args) {
        System.out.println("✅ MySQL initialized successfully");
        int cores = Runtime.getRuntime().availableProcessors();
        Vertx.vertx().deployVerticle(HttpVerticle.class.getName(), new DeploymentOptions().setInstances(cores * 2));

        SqlConfigFactory.init();
    }
}
