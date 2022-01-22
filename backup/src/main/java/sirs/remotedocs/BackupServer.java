package sirs.remotedocs;

import sirs.remotedocs.crypto.AsymmetricCryptoOperations;
import sirs.remotedocs.crypto.SymmetricCryptoOperations;
import sirs.remotedocs.exceptions.BackupServerException;

import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public class BackupServer {

    private KeyPair keyPair;
    private SecretKey secretKey;
    private PublicKey serverPublicKey;
    private byte[] serverInitializationVector;
    private final Map<Integer, Boolean> nonces = new HashMap<>();

    public BackupServer() {
        try {
            this.keyPair = AsymmetricCryptoOperations.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Failed to generate key pair for backup server.");
        }
    }

    public SecretKey getSecretKey() {
        return this.secretKey;
    }

    public byte[] getInitializationVector() {
        return this.serverInitializationVector;
    }

    public PublicKey getPublicKey(byte[] serverPublicKey) throws BackupServerException {
        if (keyPair == null)
            throw new BackupServerException("No public key available at this time.");

        try {
            this.serverPublicKey = AsymmetricCryptoOperations.getPublicKeyFromBytes(serverPublicKey);
            return this.keyPair.getPublic();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new BackupServerException("Error exchanging public keys.");
        }
    }

    protected PrivateKey getPrivateKey() throws BackupServerException {
        if (keyPair == null)
            throw new BackupServerException("Error decrypting content.");

        return this.keyPair.getPrivate();
    }

    public int handshake(byte[] secretKey, byte[] initializationVector, int nonce, byte[] signature, byte[] request)
            throws BackupServerException {
        this.secretKey = SymmetricCryptoOperations.getKeyFromBytes(secretKey);
        this.serverInitializationVector = initializationVector;

        if (this.nonces.containsKey(nonce))
            throw new BackupServerException("This nonce was already used! Possible replay attack detected.");

        try {
            boolean validSignature = AsymmetricCryptoOperations.verifySignature(
                    signature,
                    this.serverPublicKey,
                    request
            );

            if (!validSignature)
                throw new BackupServerException("Invalid server signature. Rejected.");

            this.nonces.put(nonce, true);
            return nonce + 1;
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new BackupServerException("Failed to valid server signature. Rejected.");
        }
    }

}
