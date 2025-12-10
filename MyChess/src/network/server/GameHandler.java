package network.server;

import model.*;
import network.Protocol;
import org.jetbrains.annotations.NotNull;

public class GameHandler implements Runnable {
    private final ClientHandler player1; // White
    private final ClientHandler player2; // Black
    private final Game game;

    public GameHandler(@NotNull ClientHandler player1,  @NotNull ClientHandler player2) {
        this.player1 = player1;
        this.player2 = player2;

        Player playerWhite = new Player(player1.getName(), PieceColour.WHITE);
        Player playerBlack = new Player(player2.getName(), PieceColour.BLACK);

        this.game = new Game(playerWhite, playerBlack);
        
        // Set colors and game references
        player1.setColour(PieceColour.WHITE);
        player1.setGame(this);
        player2.setColour(PieceColour.BLACK);
        player2.setGame(this);
    }

    public void handleMove(Move move, ClientHandler sender) {
        // Validate it's the sender's turn
        if (!isPlayersTurn(sender)) {
            sender.sendMessage(Protocol.sendErrorToClient("Not your turn"));
            return;
        }
        
        // Validate and apply the move
        if (!game.isValidMove(move)) {
            sender.sendMessage(Protocol.sendErrorToClient("Invalid move"));
            return;
        }
        
        game.doMove(move);
        
        // Broadcast the move to the opponent
        ClientHandler opponent = (sender == player1) ? player2 : player1;
        opponent.sendMessage(Protocol.sendMove(move));
        
        System.out.println("[S]" + sender.getName() + " moved: (" + 
                         move.getFromRow() + "," + move.getFromCol() + ") to (" + 
                         move.getToRow() + "," + move.getToCol() + ")");
        
        // Check if game is over
        if (game.isGameOver()) {
            String winner;
            if (game.isKingInCheck()) {
                // Checkmate - current player (who can't move) loses
                winner = game.getCurrentTurn().getPieceColour() == PieceColour.WHITE ? 
                         player2.getName() : player1.getName();
            } else {
                // Stalemate
                winner = "Draw";
            }
            
            String gameOverMsg = Protocol.sendGameOver(winner);
            player1.sendMessage(gameOverMsg);
            player2.sendMessage(gameOverMsg);
            
            System.out.println("[S]Game over: " + winner);
        }
    }
    
    private boolean isPlayersTurn(ClientHandler player) {
        PieceColour currentTurn = game.getCurrentTurn().getPieceColour();
        return player.getColour() == currentTurn;
    }

    @Override
    public void run() {
        // Send NEWGAME message to both players
        String newGameMsg = Protocol.sendNewGame(player1.getName(), player2.getName());
        player1.sendMessage(newGameMsg);
        player2.sendMessage(newGameMsg);
        
        System.out.println("[S]Game started: " + player1.getName() + " (White) vs " + 
                         player2.getName() + " (Black)");
        
        // Game loop is handled by client move messages, not polling
        // The thread completes after sending NEWGAME
    }

    public ClientHandler getPlayer1() {
        return player1;
    }

    public ClientHandler getPlayer2() {
        return player2;
    }
    
    public Game getGame() {
        return game;
    }
}