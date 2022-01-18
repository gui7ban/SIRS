package sirs.remotedocs.crypto;

import java.util.Random;

public class RandomOperations {
    private static String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwyz0123456789";

    public static String randomString(int length) {
        String result = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            result += CHARACTERS.charAt(random.nextInt(60));
        }

        return result;
    }
}
