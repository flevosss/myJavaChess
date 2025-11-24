package controller;

import model.Player;
import view.GraphicsBoard;

public class GameController {
    private final Game game;
    private final GraphicsBoard view;
    public GameController(Player p1, Player p2) {
        this.game = new Game(p1, p2);
        this.view = new GraphicsBoard(game.getBoard(), 85);

        GameInputHandler input = new GameInputHandler(game, view);
        view.addMouseListener(input);
        view.addMouseMotionListener(input);
    }
}
