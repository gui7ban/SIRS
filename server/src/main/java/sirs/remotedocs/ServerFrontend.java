package sirs.remotedocs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import sirs.remotedocs.backupgrpc.Backupcontract.EncryptedRequest;
import sirs.remotedocs.backupgrpc.Backupcontract.EncryptedResponse;
import sirs.remotedocs.backupgrpc.Backupcontract.PublicKeyRequest;
import sirs.remotedocs.backupgrpc.Backupcontract.PublicKeyResponse;
import sirs.remotedocs.backupgrpc.RemoteDocsBackupGrpc;

public class ServerFrontend {
    private RemoteDocsBackupGrpc.RemoteDocsBackupBlockingStub stub;
    private final ManagedChannel channel;

    public ServerFrontend(String path) {
        this.channel = ManagedChannelBuilder
                .forTarget(path)
                .build();
        this.stub = RemoteDocsBackupGrpc.newBlockingStub(this.channel);
    }

    public PublicKeyResponse getBackupServerPublicKey(PublicKeyRequest request) { return this.stub.getPublicKey(request); }

    public EncryptedResponse handshake(EncryptedRequest request) { return this.stub.handshake(request); }

    public EncryptedResponse backupServerFiles(EncryptedRequest request) { return this.stub.backupServerFiles(request); }
}
