package helpers.utils;


import org.mindrot.jbcrypt.BCrypt;

public enum PasswordUtils {

    INSTANCE;

    public String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(12));
    }

    public boolean match(String plain, String hashed) {
        return BCrypt.checkpw(plain, hashed);
    }
}
