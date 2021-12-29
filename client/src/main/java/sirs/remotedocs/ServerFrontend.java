package sirs.remotedocs;

public class ServerFrontend {
    private SILOGrpc.SILOBlockingStub stub;
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
        channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        stub = SILOGrpc.newBlockingStub(channel);
    }

    public Silo.PingResponse ctrlPing(Silo.PingRequest request) {
        return stub.ctrlPing(request);
    }

    public void channelEnd() {
        channel.shutdownNow();
    }

    public Silo.ClearResponse ctrlClear(Silo.ClearRequest request) {
        return stub.ctrlClear(request);
    }


}
