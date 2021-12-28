package sirs.remotedocs;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class ServerApp {
	public static void main(String[] args) {

		System.out.println(ServerApp.class.getSimpleName());

		try {
			if (args.length != 1) {
				System.err.println("Invalid arguments");
				System.err.printf("Usage: java %s <port>", ServerApp.class.getSimpleName());
				System.exit(-1);
			}

			int port = Integer.parseInt(args[0]);
			ServerServiceImpl serverService = new ServerServiceImpl();
			Server server = ServerBuilder
					.forPort(port)
					.addService(serverService)
					.build()
					.start();
			server.awaitTermination();
		} catch (InterruptedException | IOException e) {
			System.err.println("Something went wrong: " + e.getMessage());
		}
		
	}
}
