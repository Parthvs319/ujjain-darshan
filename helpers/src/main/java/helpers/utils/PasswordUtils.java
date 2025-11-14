package helpers.utils;


import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(12));
    }

    public static boolean match(String plain, String hashed) {
        return BCrypt.checkpw(plain, hashed);
    }
}
