package sirs.remotedocs;

public class BackUpServerApp {
    
    public static void main(String[] args) {
        System.out.println(BackUpServerApp.class.getSimpleName());
        System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		// check arguments
		if (args.length != 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", BackUpServerApp.class.getName());
            System.exit(-1);
		}

        final String host = args[0];
		final int port = Integer.parseInt(args[1]);
       

    }
}
