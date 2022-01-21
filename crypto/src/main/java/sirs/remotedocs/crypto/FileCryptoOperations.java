package sirs.remotedocs.crypto;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.security.SecureRandom;


public class FileCryptoOperations {
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public static IvParameterSpec generateIv(){
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static byte[] encryptFile(String input, IvParameterSpec iv, SecretKey key) 
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
        InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
 
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        return cipher.doFinal(input.getBytes());
    }

    public static String decryptFile(byte[] cipherText, SecretKey key, IvParameterSpec iv) 
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
        InvalidKeyException, BadPaddingException, IllegalBlockSizeException {  

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plaintext = cipher.doFinal(Base64.getDecoder().decode(cipherText));

        return new String(plaintext);
        }
}

