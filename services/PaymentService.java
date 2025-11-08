package src.services;

import src.models.sql.Trip;
import java.util.Map;
import java.util.HashMap;

/**
 * Payment stub: integrate with real gateway (Razorpay/Stripe/PayU) in production.
 */
public class PaymentService {
    public static Map<String, Object> createPaymentForTrip(Trip trip) {
        Map<String, Object> m = new HashMap<>();
        m.put("paymentId", "PAY-" + System.currentTimeMillis());
        m.put("amount", 1000); // stub amount
        m.put("currency", "INR");
        m.put("status", "created");
        return m;
    }
}
