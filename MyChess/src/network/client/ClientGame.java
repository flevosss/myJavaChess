package network.client;


import model.Game;
import model.Move;

public class ClientGame {

    private final Game game;
    private final Client client;
    private boolean isGameOver;

    public ClientGame(Client client, Game game) {
        this.client = client;
        this.game = game;
        this.isGameOver = false;
    }

    /**
     * Handle a move from the local player (called by GUI).
     * Applies the move locally and sends it to the server if valid.
     * @param move the move to make
     */
    public void handleMove(Move move) {
        if (isGameOver) {
            System.out.println("[CG]Game is already over");
            return;
        }
        
        if (game.isValidMove(move)) {
            game.doMove(move);
            client.sendMove(move);
        }
    }

    /**
     * Handle a move received from the opponent via the server.
     * @param move the move received from the server
     */
    public void handleOpponentMove(Move move) {
        if (isGameOver) {
            System.out.println("[CG]Game is already over");
            return;
        }
        
        game.doMove(move);
        System.out.println("[CG]Opponent moved: (" + move.getFromRow() + "," + move.getFromCol() + 
                         ") to (" + move.getToRow() + "," + move.getToCol() + ")");
    }

    /**
     * Handle game over notification from server
     */
    public void handleGameOver() {
        this.isGameOver = true;
        System.out.println("[CG]Game has ended");
    }

    /**
     * Get the underlying game object
     * @return the game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Check if the game is over
     * @return true if game is over
     */
    public boolean isGameOver() {
        return isGameOver || game.isGameOver();
    }
}
