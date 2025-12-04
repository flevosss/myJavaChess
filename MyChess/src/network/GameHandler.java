package network;

import controller.GameController;
import model.Game;
import model.PieceColour;
import model.Player;
import org.jetbrains.annotations.NotNull;

public class GameHandler implements Runnable {
    private final ClientHandler player1;
    private final ClientHandler player2;
    private final GameController currentGameController;
    private final Game game;

    public GameHandler(@NotNull ClientHandler player1,  @NotNull ClientHandler player2) {
        this.player1 = player1;
        this.player2 = player2;

        Player playerWhite = new Player(player1.getName(), PieceColour.WHITE);
        Player playerBlack = new Player(player2.getName(), PieceColour.BLACK);

        this.game = new Game(playerWhite, playerBlack);

        this.currentGameController = new GameController(game);
    }

    public void handleMove() {

    }

    @Override
    public void run() {
        while (!currentGameController.getGame().isGameOver) {

        }
    }

    public ClientHandler getClientHandler1() {
        return player1;
    }

    public ClientHandler getClientHandler2() {
        return player2;
    }
}