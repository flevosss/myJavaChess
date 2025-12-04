package network.client;

import model.Game;
import model.Move;
import network.Protocol;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements Runnable {

    private Socket socket;

    private PrintWriter writer;
    private BufferedReader reader;
    private ClientGame game;

    private Thread thread;


    public void connect (InetAddress address, int port) {
        try {
            //creating the socket between the server and the client.
            this.socket = new Socket(address, port);
            //initializing writer.
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            //initializing reader.
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //creating a new thread to listen for the servers responses.
            thread = new Thread(this, "ClientThread");
            thread.start();
            //sending the handshake to the server.
            handleMessage("hello");
        } catch (IOException a) {
            //if we cant establish a connection
            System.out.println("[C]Couldn't connect to the server" + "\n");
            //with the socket then the IO exception will be thrown.
        }
    }

    public void close()  {
        try {
            thread.join();
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("[C]We have got an I/O exception while trying " +
                    "to end the connection." + "\n");
        } catch (InterruptedException e) {
            System.out.println("[C]There was a problem while terminating " +
                    "the thread." + "\n");
        }
    }

    private void sendLogin(String [] parts) {
        if (parts.length < 2) {
            System.out.println("[C]You haven't specified your username!"
                    + "\n");
        } else {
            writer.write(Protocol.sendLogin(parts[1]));
        }
    }

    private void sendChallenge(String [] parts) {
        if (parts.length < 5) {
            System.out.println("parametrs not sufficient");
            return;
        }

        writer.write(Protocol.sendChallengeTo(parts[2], Integer.parseInt(parts[4])));
    }

    private void sendQueue() {
        writer.write(Protocol.sendQueue());
    }

    private void sendList(){
        writer.write(Protocol.sendList());
    }

    private void sendNewMove(String [] parts){
        if (parts.length < 4 ) {
            System.out.println("not enough parameters");
            return;
        }
        if (this.game == null) {
            System.out.println("you have to join a game to send a move!");
        }




        writer.write(Protocol.sendMove(move));
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
                sendNewMove(parts)
            }

        }
    }

    private void serverHandleNewGame (String [] parts) {
        if (parts.length < 2)
    }

    private void handleServerMessage(String message) {
        String [] parts = message.split(" ");
        String serverInstruction = parts[0].toUpperCase();

        switch (serverInstruction) {
            case "NEWGAME" -> {
                serverHandleNewGame(parts);
            }
        }
    }

    @Override
    public void run() {
        String line;
        while (true) {
            try {
                while ((line = reader.readLine()) != null) {
                    handleServerMessage(line);
                }
            } catch (IOException e) {
                System.out.println("Exception when reading from input");
            }
        }
    }
}
