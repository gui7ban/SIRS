package sirs.remotedocs;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.Scanner;

public class BackUpServerApp {
    
    public static void main(String[] args) {
        System.out.println(BackUpServerApp.class.getSimpleName());
        System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// Check arguments
		if (args.length != 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s <port>", BackUpServerApp.class.getSimpleName());
            System.exit(-1);
		}

		try {
			int port = Integer.parseInt(args[0]);
			BackupServerService backupServerService = new BackupServerService();
			Server server = ServerBuilder
					.forPort(port)
					.addService(backupServerService)
					.build()
					.start();

			System.out.println("BackupServer started on port: " + port);
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
