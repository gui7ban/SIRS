package sirs.remotedocs.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class AsymmetricCryptoOperations {
    private static final String SIGN_ALGORITHM = "SHA256withRSA";
    private static final String ENCRYPTION_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 4096;

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    public static PublicKey getPublicKeyFromString(String key)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] byteKey = Base64.getDecoder().decode(key);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(byteKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPTION_ALGORITHM);
        return keyFactory.generatePublic(publicKeySpec);
    }

    public static PrivateKey getPrivateKeyFromString(String key)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] byteKey = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(byteKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPTION_ALGORITHM);
        return keyFactory.generatePrivate(privateKeySpec);
    }

    public static byte[] encrypt(byte[] content, Key key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(content);
    }

    public static byte[] decrypt(byte[] cipheredContent, Key key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipheredContent);
    }

    public static byte[] sign(byte[] content, PrivateKey privateKey) throws
            NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(content);
        return signature.sign();
    }

    public static boolean verifySignature(byte[] content, PublicKey publicKey, byte[] signature)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signAlgorithm = Signature.getInstance(SIGN_ALGORITHM);
        signAlgorithm.initVerify(publicKey);
        signAlgorithm.update(content);
        return signAlgorithm.verify(signature);
    }
}