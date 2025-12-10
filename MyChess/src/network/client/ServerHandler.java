package network.client;

import model.Game;
import model.PieceColour;
import model.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerHandler implements Runnable {

   private final BufferedReader reader;
   private final Thread curentThread;
   private final Client client;
   private ClientGame currentGame;

   public ServerHandler(Client client) {
        try {
            this.client = client;
            this.reader = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
            currentGame = null;
            curentThread = new Thread(this, "readingFromServer");
            curentThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
   }

   public void close() throws IOException {
        reader.close();
   }

    private void handleMessage(String message) {
        String [] parts = message.split(" ");
        String serverInstruction = parts[0].toUpperCase();

        switch (serverInstruction) {
            case "NEWGAME" -> {
                serverHandleNewGame(parts);
            }
        }
    }

    private void serverHandleNewGame(String [] message) {
        if (message.length < 4 ) {
            System.out.println("missing arguments");
            return;
        }

        String whitePlayerName = message[0];
        String blackPlayerName = message[1];

        Player white = new Player(whitePlayerName, PieceColour.WHITE);
        Player black = new Player(blackPlayerName, PieceColour.BLACK);

        Game game = new Game(white,black);

        ClientGame clientGame = new ClientGame(client, game);
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                handleMessage(line);
            }
        } catch (IOException e) {
            System.out.println("Lost connection with the server: " + e.getMessage());
        }
    }
}

