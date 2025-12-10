package network.client;

import model.Game;
import model.Move;
import network.Protocol;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private Socket socket;
    private PrintWriter writer;
    private ServerHandler serverHandler; 
    private ClientGame currentGame;
    private String username;

    public void connect (InetAddress address, int port) {
        try {
            this.socket = new Socket(address, port);
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            this.serverHandler = new ServerHandler(this);
        } catch (IOException a) {
            //if we cant establish a connection
            System.out.println("[C]Couldn't connect to the server" + "\n");
            //with the socket then the IO exception will be thrown.
        }
    }

    public void close()  {
        try {
            serverHandler.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("[C]We have got an I/O exception while trying " +
                    "to end the connection." + "\n");
        }
    }

    private void sendLogin(String [] parts) {
        if (parts.length < 2) {
            System.out.println("[C]You haven't specified your username!"
                    + "\n");
        } else {
            this.username = parts[1];
            writer.println(Protocol.sendLogin(parts[1]));
        }
    }

    private void sendChallenge(String [] parts) {
        if (parts.length < 5) {
            System.out.println("parametrs not sufficient");
            return;
        }

        writer.println(Protocol.sendChallengeTo(parts[2], Integer.parseInt(parts[4])));
    }

    private void sendQueue() {
        writer.println(Protocol.sendQueue());
    }

    private void sendList(){
        writer.println(Protocol.sendList());
    }

    private void sendNewMove(String [] parts){
        if (parts.length < 5) {
            System.out.println("not enough parameters");
            return;
        }
        if (this.currentGame == null) {
            System.out.println("you have to join a game to send a move!");
            return;
        }
        
        int fromRow = Integer.parseInt(parts[1]);
        int fromCol = Integer.parseInt(parts[2]);
        int toRow = Integer.parseInt(parts[3]);
        int toCol = Integer.parseInt(parts[4]);
        
        Move move = new Move(fromRow, fromCol, toRow, toCol);
        writer.println(Protocol.sendMove(move));
    }

    private void handleMessage(String message) {
        String [] parts = message.split(" ");
        String clientInstruction = parts[0].toUpperCase();

        switch (clientInstruction) {
            case "LOGIN" -> {
               sendLogin(parts);
            }

            case "CHALLENGE" -> {
                sendChallenge(parts);
            }

            case "QUEUE" -> {
                sendQueue();
            }

            case "LIST" -> {
                sendList();
            }

            case "MOVE" -> {
                sendNewMove(parts);
            }
        }
    }

    public Socket getSocket() {
        return this.socket;
    }
    
    public void setCurrentGame(ClientGame game) {
        this.currentGame = game;
    }
    
    public ClientGame getCurrentGame() {
        return this.currentGame;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void sendMove(Move move) {
        if (currentGame == null) {
            System.out.println("[C]Not in a game");
            return;
        }
        writer.println(Protocol.sendMove(move));
    }
}