package network;

import model.*;

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
            case "HELLO" -> {
                handleHello(parts);
            }

            case "CHALLENGE" -> {

            }

            case "QUEUE" -> {

            }

            case "LIST" -> {

            }

            case "MOVE" -> {
                handleNewMove(parts);
            }
        }
    }
    public String sendHello() {
        return Protocol.serverSendHello();
    }

    private void handleNewMove(String [] input) {
        if (input.length < 2) {
            sendError("Move index is not provided");
            return;
        }
        if (this.currentGame == null) {
            sendError("You are not in a game");
            return;
        }

        //MOVE~<fromCol>~<fromRow>~<toRow>~<ToCol>
//        Piece piece =
//        Move move = new Move(input[1], input[2], input[3], input[4], );
//        this.currentGame.handleMove(move, this);
    }

    public String getName() {
        return this.username;
    }

    public boolean verifyMove() {
        return false;
    }

    private void handleHello(String [] input) {
        if (input.length < 2) {
            sendError("Username is not provided");
            return;
        }
        this.username = input[1];
    }

    private void sendError(String message) {
        out.println(Protocol.sendErrorToClient(message));
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
