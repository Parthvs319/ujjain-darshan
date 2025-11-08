package src;

/**
 * Ebean initialization helper (real config via application.conf)
 */
public class EbeanInit {
    public static void init() {
        // Ebean will auto-initialize based on agent or configuration in production.
        System.out.println("Ebean init (ensure agent is configured in production)");
    }
}
