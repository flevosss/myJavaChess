package network;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements Runnable {

    private Socket socket;

    private PrintWriter writer;
    private BufferedReader reader;

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

    private void handleLogin(String [] parts) {
        if (parts.length < 2) {
            System.out.println("[C]You haven't specified your username!"
                    + "\n");
        } else {

        }
    }

    private void handleMessage(String message) {
        String [] parts = message.split(" ");
        String clientInstruction = parts[0].toUpperCase();

        switch (clientInstruction) {
            case "LOGIN" -> {
               handleLogin(parts);
            }

        }
    }

    @Override
    public void run() {

    }
}
