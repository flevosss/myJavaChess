package network.server;

import model.*;
import network.Protocol;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private Game game;
    private final Server server;

    private BufferedReader in;
    private PrintWriter out;

    private PieceColour colour;
    private GameHandler currentGame;

    private String username;

    public ClientHandler(Server server, Socket socket) {
        this.socket = socket;
        this.server = server;
        this.colour = PieceColour.NOT_ASSIGNED;
        this.currentGame = null;
        this.username = null;

        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        } catch (IOException e) {
            System.out.println("Error creating socket communication");
        }
    }

    public void handleClientMessage(String clientInput) {
        String [] parts = clientInput.split("~");
        String command = parts[0];

        switch (command){
            case "LOGIN" -> {
                handleLogin(parts);
            }

            case "CHALLENGE" -> {
                // TODO: implement challenge functionality
            }

            case "QUEUE" -> {
                handleQueue();
            }

            case "LIST" -> {
                // TODO: implement list functionality
            }

            case "MOVE" -> {
                handleMove(parts);
            }
        }
    }
    public void handleQueue() {
        if (username == null) {
            sendError("You must login first");
            return;
        }
        server.addClientToQueue(this);
        System.out.println("[S]" + username + " joined the queue");
    }

    private void handleMove(String [] input) {
        if (input.length < 5) {
            sendError("Invalid move format");
            return;
        }
        if (this.currentGame == null) {
            sendError("You are not in a game");
            return;
        }

        try {
            int fromRow = Integer.parseInt(input[1]);
            int fromCol = Integer.parseInt(input[2]);
            int toRow = Integer.parseInt(input[3]);
            int toCol = Integer.parseInt(input[4]);
            
            Move move = new Move(fromRow, fromCol, toRow, toCol);
            currentGame.handleMove(move, this);
        } catch (NumberFormatException e) {
            sendError("Invalid move coordinates");
        }
    }

    private void handleLogin(String [] input) {
        if (input.length < 2) {
            sendError("Username is not provided");
            return;
        }
        this.username = input[1];
        out.println(Protocol.serverSendHello());
        System.out.println("[S]" + username + " logged in");
    }

    private void sendError(String message) {
        out.println(Protocol.sendErrorToClient(message));
    }
    
    public void sendMessage(String message) {
        out.println(message);
    }
    
    public void setGame(GameHandler game) {
        this.currentGame = game;
    }
    
    public void setColour(PieceColour colour) {
        this.colour = colour;
    }
    
    public PieceColour getColour() {
        return this.colour;
    }
    
    public String getName() {
        return this.username;
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
               handleClientMessage(line);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        }
    }
}
