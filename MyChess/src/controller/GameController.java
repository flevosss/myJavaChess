package controller;

import model.*;
import view.Dialogs.GameOverDialog;
import view.Dialogs.PromotionDialog;
import view.Panels.GraphicsBoard;

import java.awt.*;

public class GameController implements IGameController {
    protected final Game game;
    protected final GraphicsBoard view;

    public GameController(Game game) {
        this.game = game;
        this.view = new GraphicsBoard(game.getBoard(), 88);

        GameInputHandler input = new GameInputHandler(this, view);
        view.addMouseListener(input);
        view.addMouseMotionListener(input);
    }

    public void handleMove(Move move) {
        game.doMove(move);
        afterMoveApplied(move);
    }

    protected void showGameOverDialog(String message, String winner) {
        java.awt.Window parent =
                javax.swing.SwingUtilities.getWindowAncestor(this.getView());

        GameOverDialog dialog = new GameOverDialog(parent, message, winner);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    protected void afterMoveApplied(Move move) {
        Piece moved = game.getBoard().getPiece(move.getToRow(), move.getToCol());

        if (game.needsPromotion(moved)) {
            PieceType choice = askPromotionType(moved);
            game.promotePawn(moved, choice);
        }

        view.repaint();

        if (game.isGameOver()) {
            String msg;
            if (game.isKingInCheck()) {
                msg = "Checkmate!\n";
                showGameOverDialog(msg, game.getBoard().getPiece(move.getToRow(), move.getToCol()).getColour().toString());
            } else {
                msg = "Stalemate!\n";
                showGameOverDialog(msg, null);
            }
        }
    }

    protected PieceType askPromotionType(Piece pawn) {
        java.awt.Window parentWindow =
                javax.swing.SwingUtilities.getWindowAncestor(view);

        PromotionDialog dialog =
                new PromotionDialog(parentWindow, pawn.getColour(), view.getTileSize());

        try {
            Point boardOnScreen = view.getLocationOnScreen();
            int tile = view.getTileSize();

            int pawnX = boardOnScreen.x + pawn.getColumn() * tile;
            int pawnY = boardOnScreen.y + pawn.getRow() * tile;

            dialog.setLocation(pawnX, pawnY);
        } catch (IllegalComponentStateException ex) {
            dialog.setLocationRelativeTo(parentWindow);
        }

        return dialog.selectPieceType();
    }

    public GraphicsBoard getView() {
        return view;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public boolean canSelectPiece(Piece piece) {
        return true;
    }
}
