package network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Class for the chess server, hosting a server, and provides
 * multiple games at the same time.
 */
public class Server implements Runnable {

    //the socket of the server
    private ServerSocket serverSocket;
    //the game queue
    private final Queue<ClientHandler> gameQueue = new LinkedList<>();

    /**
     * Start method to be called from the main method of the server.
     * It creates a server socket with the given port,
     * to receive connections.
     * @param port the port of the server.
     */
    public void start(int port)  {
        try {
            this.serverSocket = new ServerSocket(port);
            //starting a thread to check constantly for new connections.
            new Thread(this, "acceptingConnections").start();
            //starting a thread to check for new games.
            new Thread(this::waitingForQueue, "gameQueue").start();
        } catch (IOException e) {
            System.out.println("[S]Error creating the socket for the server." +
                    " Maybe the port is reserved?");
        }
    }

    /**
     * The method that the acceptingConnections thread runs,
     * which constantly accepting connections from clients,
     * and creates a new client handler for each of them.
     */
    @Override
    public void run() {
        try {
            while(!serverSocket.isClosed()) {
                //accepting new connections/clients.
                Socket socket = serverSocket.accept();
                System.out.println("[S]Got a connection from " +
                        socket.getInetAddress().getHostAddress());
                //creating a new thread for each client handler
                new Thread(new ClientHandler(this, socket),
                        "clientHandlerThread").start();
            }
        } catch (IOException e) {
            System.out.println("[S]Server closed");
        }
    }

    public synchronized void challenge() {

    }

    /**
     * Synchronized method to wait in the queue
     */
    public synchronized void waitingForQueue()  {
        //sleeping until notified by the readyToQue method.
        while(!serverSocket.isClosed()) {
            while (gameQueue.size() < 2) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("[S]Interrupted exception");
                }
            }
            //pops the first two players in the queue.
            ClientHandler player1 = gameQueue.poll();
            ClientHandler player2 = gameQueue.poll();

            if (player1 == null || player2 == null) {
                System.out.println("[S] One of the two players is null");
                continue;
            }

            new Thread(new GameHandler(player1, player2),
                    "gameHandlerThread").start();
        }
    }

    /**
     * Method to be called by the client handler to
     * put a client to the queue.
     * @param client the client to be added to the queue.
     */
    public synchronized void addClientToQueue(ClientHandler client) {
        this.gameQueue.add(client);
        if (this.gameQueue.size()>=2) notifyAll();
    }

    /**
     * Receives a client as argument, and checks if this client is in
     * the queue.
     * @param client the client to be checked.
     * @return true if this user is in queue, false otherwise.
     */
    public boolean isClientInQueue(ClientHandler client) {
        return gameQueue.contains(client);
    }

    /**
     * Method to remove a client from the queue.
     * @param client to be removes from the queue.
     */
    public synchronized void removeClientFromQueue(ClientHandler client) {
        gameQueue.remove(client);
    }

    /**
     * Stop method to stop the server from running.
     */
    public void stop()  {
        try {
            gameQueue.clear();
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("[S]Error while closing the socket");
        }
    }

    /**
     * Method to return the port number to which the server,
     * is running on.
     * @return the port number of the server.
     */
    public int getPort() {
        return serverSocket.getLocalPort();
    }
}
