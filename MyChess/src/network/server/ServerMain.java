package network.server;

import java.util.Scanner;

/**
 * Main class to start the Chess server.
 */
public class ServerMain {

    public static void main(String[] args) {
        Server server = new Server();
        int port = 8888;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("[S]Invalid port number provided. Using default port 8888.");
            }
        }
        
        System.out.println("[S]Starting Chess Server on port " + port + "...");
        server.start(port);
        System.out.println("[S]Server is running on port " + server.getPort());
        System.out.println("[S]Type 'stop' to shut down the server.");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("stop")) {
                System.out.println("[S]Shutting down server...");
                server.stop();
                scanner.close();
                System.out.println("[S]Server stopped.");
                break;
            }
        }
    }
}
