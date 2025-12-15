package controller;

import model.Game;
import model.Move;
import model.Piece;
import view.Panels.GraphicsBoard;

/**
 * Interface for game controllers to handle moves and game flow.
 */
public interface IGameController {
    void handleMove(Move move);
    Game getGame();
    GraphicsBoard getView();
    boolean canSelectPiece(Piece piece);
}
