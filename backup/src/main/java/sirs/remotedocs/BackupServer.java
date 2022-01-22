package sirs.remotedocs;

import sirs.remotedocs.crypto.AsymmetricCryptoOperations;
import sirs.remotedocs.exceptions.BackupServerException;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class BackupServer {

    private KeyPair keyPair;

    public BackupServer() {
        try {
            this.keyPair = AsymmetricCryptoOperations.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Failed to generate key pair for backup server.");
        }
    }

    public PublicKey getPublicKey() throws BackupServerException {
        if (keyPair == null)
            throw new BackupServerException("No public key available at this time.");

        return this.keyPair.getPublic();
    }
}
