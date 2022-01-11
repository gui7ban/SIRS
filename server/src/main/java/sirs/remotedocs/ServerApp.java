package sirs.remotedocs;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class ServerApp {
	public static void main(String[] args) {
		Logger logger = new Logger("Server", "Main");

		System.out.println(ServerApp.class.getSimpleName());
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		if (args.length != 1) {
			System.err.println("Invalid arguments");
			System.err.printf("Usage: java %s <port>", ServerApp.class.getSimpleName());
			System.exit(-1);
		}

		try {

		
			int port = Integer.parseInt(args[0]);
			ServerServiceImpl serverService = new ServerServiceImpl();
			Server server = ServerBuilder
					.forPort(port)
					.addService(serverService)
					.build()
					.start();

			// Server threads are running in the background.
			logger.log("Server started on port: " + port);

			server.awaitTermination();
			
		} catch (InterruptedException | IOException e) {
			System.err.println("Something went wrong: " + e.getMessage());
		}
		
	}
}
