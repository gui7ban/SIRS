package sirs.remotedocs;

import io.grpc.StatusRuntimeException;
import sirs.remotedocs.grpc.Contract.*;

public class ClientApp {
    public static void main(String[] args) {
        System.out.println(ClientApp.class.getSimpleName());
        System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		// check arguments
		if (args.length != 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", ClientApp.class.getName());
            System.exit(-1);
		}

        final String host = args[0];
		final int port = Integer.parseInt(args[1]);
        ServerFrontend frontend = new ServerFrontend(host, port);
		PingRequest pingRequest = PingRequest.newBuilder().build();
		PingResponse pingResponse = frontend.ping(pingRequest);
		System.out.println(pingResponse.getOutputText());

		RegisterRequest registerRequest = RegisterRequest.newBuilder().setUsername("Gui").setPassword("123456789").build();
		try {
			RegisterResponse registerResponse = frontend.register(registerRequest);
		}
		catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " +
			e.getStatus().getDescription());
		}
		LoginRequest loginRequest = LoginRequest.newBuilder().setUsername("Gui").setPassword("123456789").build();
		try {
			LoginResponse loginResponse = frontend.login(loginRequest);
		}
		catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " +
			e.getStatus().getDescription());
		}
		frontend.channelEnd();
    }
}