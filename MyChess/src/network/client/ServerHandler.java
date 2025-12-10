package network.client;

import model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerHandler implements Runnable {

   private final BufferedReader reader;
   private final Thread curentThread;
   private final Client client;

   public ServerHandler(Client client) {
        try {
            this.client = client;
            this.reader = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
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
        String [] parts = message.split("~");
        String serverInstruction = parts[0].toUpperCase();

        switch (serverInstruction) {
            case "HELLO" -> {
                System.out.println("[C]Connected to server");
            }
            case "NEWGAME" -> {
                serverHandleNewGame(parts);
            }
            case "MOVE" -> {
                serverHandleMove(parts);
            }
            case "GAMEOVER" -> {
                serverHandleGameOver(parts);
            }
            case "ERROR" -> {
                serverHandleError(parts);
            }
            default -> {
                System.out.println("[C]Unknown message from server: " + message);
            }
        }
    }

    private void serverHandleNewGame(String [] message) {
        if (message.length < 3) {
            System.out.println("[C]Missing arguments for NEWGAME");
            return;
        }

        String whitePlayerName = message[1];
        String blackPlayerName = message[2];

        Player white = new Player(whitePlayerName, PieceColour.WHITE);
        Player black = new Player(blackPlayerName, PieceColour.BLACK);

        Game game = new Game(white, black);

        ClientGame clientGame = new ClientGame(client, game);
        client.setCurrentGame(clientGame);
        
        System.out.println("[C]New game started: " + whitePlayerName + " (White) vs " + blackPlayerName + " (Black)");
    }
    
    private void serverHandleMove(String [] message) {
        if (message.length < 5) {
            System.out.println("[C]Invalid move message from server");
            return;
        }
        
        ClientGame game = client.getCurrentGame();
        if (game == null) {
            System.out.println("[C]Received move but not in a game");
            return;
        }
        
        try {
            int fromRow = Integer.parseInt(message[1]);
            int fromCol = Integer.parseInt(message[2]);
            int toRow = Integer.parseInt(message[3]);
            int toCol = Integer.parseInt(message[4]);
            
            Move move = new Move(fromRow, fromCol, toRow, toCol);
            game.handleOpponentMove(move);
        } catch (NumberFormatException e) {
            System.out.println("[C]Invalid move coordinates from server");
        }
    }
    
    private void serverHandleGameOver(String [] message) {
        if (message.length < 2) {
            System.out.println("[C]Game over");
        } else {
            System.out.println("[C]Game over: " + message[1]);
        }
        
        ClientGame game = client.getCurrentGame();
        if (game != null) {
            game.handleGameOver();
        }
    }
    
    private void serverHandleError(String [] message) {
        if (message.length < 2) {
            System.out.println("[C]Server error (no details)");
        } else {
            System.out.println("[C]Server error: " + message[1]);
        }
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

