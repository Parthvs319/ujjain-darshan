package models.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

/**
 * Very simple OTP service stub. Replace with Twilio or other provider in production.
 */
public enum OtpService {

    INSTANCE;

    private static final Map<String, String> otpStore = new ConcurrentHashMap<>();
    private static final Random rnd = new Random();

    public String sendOtp(String mobile) {
        String otp = String.format("%04d", rnd.nextInt(10000));
        otpStore.put(mobile, otp);
        System.out.println("OTP for " + mobile + " = " + otp + " (DEV ONLY)");
        return otp;
    }

    public boolean verifyOtp(String mobile, String otp) {
        String stored = otpStore.get(mobile);
        if (stored != null && stored.equals(otp)) {
            otpStore.remove(mobile);
            return true;
        }
        return false;
    }
}
