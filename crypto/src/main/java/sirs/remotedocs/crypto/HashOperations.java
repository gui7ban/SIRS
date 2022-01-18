package sirs.remotedocs.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashOperations {
    private static final String HASH_ALGORITHM = "SHA-256";

    public static String digest(String message) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
        return Base64.getEncoder().encodeToString(md.digest(message.getBytes()));
    }

    public static boolean verifyDigest(String message, String digest) throws NoSuchAlgorithmException {
        return digest.equals(digest(message));
    }
}
