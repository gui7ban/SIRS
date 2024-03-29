package sirs.remotedocs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import sirs.remotedocs.grpc.RemoteDocsGrpc;
import sirs.remotedocs.grpc.Contract.*;


public class ServerFrontend {
    private final RemoteDocsGrpc.RemoteDocsBlockingStub stub;
    private final ManagedChannel channel;
    private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

    /** Helper method to print debug messages. */
    private static void debug(String debugMessage) {
        if (DEBUG_FLAG)
            System.err.println(debugMessage);
    }

    public ServerFrontend(String host, int port) {
        final String target = host + ":" + port;
        debug("Target:" + target);
        channel = ManagedChannelBuilder
                .forTarget(target)
                .useTransportSecurity()
                .build();
        stub = RemoteDocsGrpc.newBlockingStub(channel);
    }

    public PingResponse ping(PingRequest request) { return stub.ping(request); }

    public RegisterResponse register(RegisterRequest request) { return stub.register(request); }

    public LoginResponse login(LoginRequest request) { return stub.login(request); }

    public UpdateFileNameResponse updateFileName(UpdateFileNameRequest request) { return stub.updateFileName(request); }

    public CreateFileResponse createFile(CreateFileRequest request) { return stub.createFile(request); }

    public UploadResponse upload(UploadRequest request) { return stub.upload(request); }

    public DownloadResponse download(DownloadRequest request) { return stub.download(request); }

    public GetDocumentsResponse getDocumentsList(GetDocumentsRequest request) { return stub.getDocumentsList(request); }

    public DeleteFileResponse deleteFile(DeleteFileRequest request) { return stub.deleteFile(request); }
    
    public LogoutResponse logout(LogoutRequest request) { return stub.logout(request); }

    public SharedDocUsersResponse docUsersList(SharedDocUsersRequest request){ return stub.docUsersList(request); }
    
    public UpdatePermissionUserResponse updatePermission(UpdatePermissionUserRequest request){ return stub.updatePermission(request); }
    
    public AddPermissionUserResponse addPermission(AddPermissionUserRequest request){ return stub.addPermission(request); }
    
    public DeletePermissionUserResponse deletePermission(DeletePermissionUserRequest request){ return stub.deletePermission(request); }

    public GetPublicKeyResponse getPublicKey(GetPublicKeyRequest request) { return stub.getPublicKey(request); }

    public void channelEnd() {
        channel.shutdownNow();
    }
}
