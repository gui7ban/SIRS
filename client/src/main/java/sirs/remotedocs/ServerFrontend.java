package sirs.remotedocs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import sirs.remotedocs.grpc.RemoteDocsGrpc;
import sirs.remotedocs.grpc.Contract.*;

import java.rmi.Remote;

public class ServerFrontend {
    private RemoteDocsGrpc.RemoteDocsBlockingStub stub;
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

    public PingResponse ping(PingRequest request) {
        return stub.ping(request);
    }

    /*
    public LoginResponse login(LoginRequest request) {
        return stub.login(request);
    }

    public RegisterResponse register(RegisterRequest request) {
        return stub.register(request);
    }*/

    public void channelEnd() {
        channel.shutdownNow();
    }
}
