import helpers.blueprint.*;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class Application {
    public static void main(String[] args) {
        int cores = Runtime.getRuntime().availableProcessors();
        Vertx.vertx().deployVerticle(HttpVerticle.class.getName(), new DeploymentOptions().setInstances(cores * 2));

        SqlConfigFactory.init();
        System.out.println("✅ MySQL initialized successfully");
    }
}
