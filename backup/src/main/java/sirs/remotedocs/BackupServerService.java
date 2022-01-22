package sirs.remotedocs;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import sirs.remotedocs.backupgrpc.Backupcontract.*;
import sirs.remotedocs.backupgrpc.RemoteDocsBackupGrpc;
import sirs.remotedocs.exceptions.BackupServerException;

import java.security.PublicKey;

import static io.grpc.Status.INVALID_ARGUMENT;

public class BackupServerService extends RemoteDocsBackupGrpc.RemoteDocsBackupImplBase {

    private final BackupServer backupServer = new BackupServer();

    @Override
    public void getPublicKey(PublicKeyRequest request, StreamObserver<PublicKeyResponse> responseObserver) {
        try {
            PublicKey publicKey = this.backupServer.getPublicKey();
            PublicKeyResponse response = PublicKeyResponse
                    .newBuilder()
                    .setKey(ByteString.copyFrom(publicKey.getEncoded()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BackupServerException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
