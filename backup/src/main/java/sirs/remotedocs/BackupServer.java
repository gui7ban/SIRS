package sirs.remotedocs;

import sirs.remotedocs.backupgrpc.Backupcontract;
import sirs.remotedocs.crypto.AsymmetricCryptoOperations;
import sirs.remotedocs.crypto.HashOperations;
import sirs.remotedocs.crypto.SymmetricCryptoOperations;
import sirs.remotedocs.exceptions.BackupServerException;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class BackupServer {

    private static final String FILES_DIR = "files/";
    private KeyPair keyPair;
    private SecretKey secretKey;
    private PublicKey serverPublicKey;
    private byte[] serverInitializationVector;
    private final Map<Integer, Boolean> nonces = new HashMap<>();
    private int currentNonce = 0;

    public BackupServer() {
        try {
            File f = new File(FILES_DIR);
            if(!f.mkdir())
                System.out.println("Error creating files directory.");
            this.keyPair = AsymmetricCryptoOperations.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Failed to generate key pair for backup server.");
            System.exit(-1);
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

    protected int getNextNonce() {
        return this.currentNonce + 1;
    }

    public int handshake(byte[] secretKey, byte[] initializationVector, int nonce, byte[] signature, byte[] request)
            throws BackupServerException {
       
        if (this.nonces.containsKey(nonce))
            throw new BackupServerException("This nonce was already used! Possible replay attack detected.");

        try {
            boolean validSignature = AsymmetricCryptoOperations.verifySignature(
                    request,
                    this.serverPublicKey,
                    signature
            );

            if (!validSignature)
                throw new BackupServerException("Invalid server signature. Rejected.");
            
            this.secretKey = SymmetricCryptoOperations.getKeyFromBytes(secretKey);
            this.serverInitializationVector = initializationVector;
            this.nonces.put(nonce, true);
            this.currentNonce = nonce;
            return this.getNextNonce();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new BackupServerException("Failed to validate server signature. Rejected: " + e.getMessage());
        }
    }

    private void saveFile(int id, byte[] content) throws IOException {
        File file = new File(FILES_DIR + id);
        if (!file.exists())
            file.createNewFile();

        FileOutputStream out = new FileOutputStream(file, false);
        out.write(content);
        out.close();

        System.out.println("Saving file with id " + id);
    }

    public List<Integer> saveFiles(List<Backupcontract.FileInfo> filesInfo, int nonce, byte[] signature, byte[] request)
            throws BackupServerException {
        if (this.nonces.containsKey(nonce))
            throw new BackupServerException("This nonce was already used! Possible replay attack detected.");

        try {
            boolean validSignature = AsymmetricCryptoOperations.verifySignature(
                    request,
                    this.serverPublicKey,
                    signature
            );

            if (!validSignature)
                throw new BackupServerException("Invalid server signature. Rejected.");

            List<Integer> corruptedFiles = new ArrayList<>();
            for (Backupcontract.FileInfo fileInfo: filesInfo) {
                String fileDigest = HashOperations.digest(fileInfo.getContent().toByteArray());
                if (!fileDigest.equals(fileInfo.getDigest()))
                    corruptedFiles.add(fileInfo.getId());
                else
                    this.saveFile(fileInfo.getId(), fileInfo.getContent().toByteArray());
            }
            
            this.nonces.put(nonce, true);
            this.currentNonce = nonce;
            return corruptedFiles;
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | IOException e) {
            throw new BackupServerException("Failed to validate server signature. Rejected: " + e.getMessage());
        }

    }

}

