package sirs.remotedocs;

import sirs.remotedocs.grpc.Contract.*;

public class ClienteApp {
    /*public static void main(String[] args) {
        System.out.println(ClienteApp.class.getSimpleName());
        System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		// check arguments
		if (args.length != 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", ClienteApp.class.getName());
            System.exit(-1);
		}

        final String host = args[0];
		final int port = Integer.parseInt(args[1]);
        ServerFrontend frontend = new ServerFrontend(host, port);
		PingRequest pingRequest = PingRequest.newBuilder().build();
		PingResponse pingResponse = frontend.ping(pingRequest);
		System.out.println(pingResponse.getOutputText());


		frontend.channelEnd();
    }*/
}