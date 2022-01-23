package sirs.remotedocs.crypto;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.security.SecureRandom;



public class HashOperations {
    private static final String HASH_ALGORITHM = "SHA-256";

    public static String digest(String message, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
        if (salt != null) {
            md.update(salt);
        }
  
        return Base64.getEncoder().encodeToString(md.digest(message.getBytes()));
    }

    public static String digest(byte[] content) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
        return Base64.getEncoder().encodeToString(md.digest(content));
    }

    public static boolean verifyDigest(String message, String digest, byte[] salt) throws NoSuchAlgorithmException {
           return digest.equals(digest(message, salt));
    }

    public static byte[] generateSalt(){
        return new SecureRandom().generateSeed(16);
    }
}
