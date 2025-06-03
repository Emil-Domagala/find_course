package emil.find_course.common.util;

import java.security.SecureRandom;

public class TokenGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateToken6NumCharToken() {
        StringBuilder token = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            token.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return token.toString();
    }

    public static String generateToken24NumCharToken() {
        StringBuilder token = new StringBuilder(24);
        for (int i = 0; i < 24; i++) {
            token.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return token.toString();
    }
}
