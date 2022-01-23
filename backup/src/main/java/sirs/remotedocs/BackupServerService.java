package sirs.remotedocs;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.stub.StreamObserver;
import sirs.remotedocs.backupgrpc.Backupcontract.*;
import sirs.remotedocs.backupgrpc.RemoteDocsBackupGrpc;
import sirs.remotedocs.crypto.AsymmetricCryptoOperations;
import sirs.remotedocs.crypto.SymmetricCryptoOperations;
import sirs.remotedocs.exceptions.BackupServerException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.util.List;

import static io.grpc.Status.ABORTED;

public class BackupServerService extends RemoteDocsBackupGrpc.RemoteDocsBackupImplBase {

    private final BackupServer backupServer = new BackupServer();

    @Override
    public void getPublicKey(PublicKeyRequest request, StreamObserver<PublicKeyResponse> responseObserver) {
        try {
            PublicKey publicKey = this.backupServer.getPublicKey(request.getPublicKey().toByteArray());
            PublicKeyResponse response = PublicKeyResponse
                    .newBuilder()
                    .setKey(ByteString.copyFrom(publicKey.getEncoded()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BackupServerException e) {
            responseObserver.onError(ABORTED.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void handshake(EncryptedRequest request, StreamObserver<EncryptedResponse> responseObserver) {
        try {
            PrivateKey privateKey = this.backupServer.getPrivateKey();
            HandshakeRequest handshakeRequest = HandshakeRequest.parseFrom(
                    AsymmetricCryptoOperations.decrypt(request.getRequest().toByteArray(), privateKey)
            );

            // Calculate nonce to return in the response.
            int nextNonce = this.backupServer.handshake(
                    handshakeRequest.getSecretKey().toByteArray(),
                    handshakeRequest.getInitializationVector().toByteArray(),
                    handshakeRequest.getNonce(),
                    request.getSignature().toByteArray(),
                    handshakeRequest.toByteArray()
            );

            // Set new nonce in the response.
            HandshakeResponse handshakeResponse = HandshakeResponse.newBuilder().setNonce(nextNonce).build();
            byte[] handshakeResponseBytes = handshakeResponse.toByteArray();

            // Encrypt the response with the secret key.
            byte[] encryptedHandshakeResponse = SymmetricCryptoOperations.encrypt(
                    handshakeResponseBytes,
                    this.backupServer.getInitializationVector(),
                    this.backupServer.getSecretKey()
            );

            // Sign the digest of the unencrypted request with the backup server private key.
            byte[] signedResponse = AsymmetricCryptoOperations.sign(
                    handshakeResponseBytes,
                    this.backupServer.getPrivateKey()
            );

            // Send the response to the server.
            EncryptedResponse response = EncryptedResponse
                    .newBuilder()
                    .setResponse(ByteString.copyFrom(encryptedHandshakeResponse))
                    .setSignature(ByteString.copyFrom(signedResponse))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BackupServerException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException
                | BadPaddingException | InvalidKeyException | InvalidProtocolBufferException
                | InvalidAlgorithmParameterException | SignatureException e) {
            responseObserver.onError(ABORTED.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void backupServerFiles(EncryptedRequest request, StreamObserver<EncryptedResponse> responseObserver) {
        try {
            FilesRequest filesRequest = FilesRequest.parseFrom(
                    SymmetricCryptoOperations.decrypt(
                            request.getRequest().toByteArray(),
                            this.backupServer.getSecretKey(),
                            new IvParameterSpec(this.backupServer.getInitializationVector())
                    )
            );

            List<Integer> corruptedFiles = this.backupServer.saveFiles(
                    filesRequest.getFilesList(),
                    filesRequest.getNonce(),
                    request.getSignature().toByteArray(),
                    filesRequest.toByteArray()
            );

            FilesResponse filesResponse = FilesResponse.newBuilder().addAllIds(corruptedFiles).build();

            // Encrypt Response
            byte[] encryptedFilesResponse = SymmetricCryptoOperations.encrypt(
                    filesResponse.toByteArray(),
                    this.backupServer.getInitializationVector(),
                    this.backupServer.getSecretKey()
            );

            // Sign Response
            byte[] signedResponse = AsymmetricCryptoOperations.sign(
                    filesResponse.toByteArray(),
                    this.backupServer.getPrivateKey()
            );

            EncryptedResponse encryptedResponse = EncryptedResponse
                    .newBuilder()
                    .setResponse(ByteString.copyFrom(encryptedFilesResponse))
                    .setSignature(ByteString.copyFrom(signedResponse))
                    .build();

            responseObserver.onNext(encryptedResponse);
            responseObserver.onCompleted();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | InvalidKeyException | BadPaddingException | IllegalBlockSizeException
                | InvalidProtocolBufferException | BackupServerException | SignatureException e) {
            responseObserver.onError(ABORTED.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
