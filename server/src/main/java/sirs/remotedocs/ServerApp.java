package sirs.remotedocs;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ServerApp {

	private static String CERTIFICATE_CHAIN_FILE = "resources/cert.pem";
	private static String CERTIFICATE_PRIVATE_KEY_FILE = "resources/unencrypted_key.pem";
	// TODO: Move private key to keystore, eventually.

	public static void main(String[] args) {
		Logger logger = new Logger("Server", "Main");

		System.out.println(ServerApp.class.getSimpleName());
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		if (args.length != 2) {
			System.err.println("Invalid arguments");
			System.err.printf("Usage: java %s <port> <backupServerPath>", ServerApp.class.getSimpleName());
			System.exit(-1);
		}

		try {
			int port = Integer.parseInt(args[0]);
			ServerServiceImpl serverService = new ServerServiceImpl(args[1]);
			Server server = ServerBuilder
					.forPort(port)
					.addService(serverService)
					.useTransportSecurity(new File(CERTIFICATE_CHAIN_FILE), new File(CERTIFICATE_PRIVATE_KEY_FILE))
					.build()
					.start();

			// Server threads are running in the background.
			logger.log("Server started on port: " + port);
			
			new Thread(() -> {
				System.out.println("Press enter to shutdown...");
				new Scanner(System.in).nextLine();
				server.shutdown();
			}).start();
			
			server.awaitTermination();
			
		} catch (InterruptedException | IOException e) {
			System.err.println("Something went wrong: " + e.getMessage());
		}
		
	}
}
