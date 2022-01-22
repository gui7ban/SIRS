package sirs.remotedocs.crypto;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.security.SecureRandom;


public class SymmetricCryptoOperations {
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public static SecretKey getKeyFromBytes(byte[] secretKey) {
        return new SecretKeySpec(secretKey,"AES");
    }

    public static IvParameterSpec generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static byte[] encrypt(byte[] input, byte[] iv, SecretKey key)
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
        InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
 
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        return cipher.doFinal(input);
    }

    public static byte[] decrypt(byte[] cipherText, SecretKey key, IvParameterSpec iv)
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
        InvalidKeyException, BadPaddingException, IllegalBlockSizeException {  

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(Base64.getDecoder().decode(cipherText));
    }
}

