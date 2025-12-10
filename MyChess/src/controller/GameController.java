package controller;

import model.*;
import view.Dialogs.GameOverDialog;
import view.Dialogs.PromotionDialog;
import view.Panels.GraphicsBoard;

import java.awt.*;

public class GameController {
    private final Game game;
    private final GraphicsBoard view;

    public GameController(Game game) {
        this.game = game;
        this.view = new GraphicsBoard(game.getBoard(), 85);

        GameInputHandler input = new GameInputHandler(this, view);
        view.addMouseListener(input);
        view.addMouseMotionListener(input);
    }

    public void handleMove(Move move) {
        game.doMove(move);

        Piece moved = game.getBoard().getPiece(move.getToRow(), move.getToCol());

        if (game.needsPromotion(moved)) {
            PieceType choice = askPromotionType(moved);
            game.promotePawn(moved, choice);
        }

        if (game.isGameOver()) {
            String msg;
            if (game.isKingInCheck()) {
                msg = "Checkmate!\n" + game.getCurrentTurn() + " has no legal moves.";
            } else {
                msg = "Stalemate!\n" + game.getCurrentTurn() + " has no legal moves.";
            }
            showGameOverDialog(msg);
        }

        view.repaint();
    }

    private void handlePawnPromotion(Piece pawn) {
        if (pawn.getType() != PieceType.PAWN) {
            return;
        }

        int pawnRow = pawn.getRow();
        PieceColour colour = pawn.getColour();

        //white pawns promote on row 0, black pawns on row 7
        boolean promote =
                (colour == PieceColour.WHITE && pawnRow == 0) ||
                        (colour == PieceColour.BLACK && pawnRow == 7);

        if (!promote) {
            return;
        }

        PieceType newType = askPromotionType(pawn);

        game.promotePawn(pawn, newType);
    }

    public void showGameOverDialog(String message) {
        java.awt.Window parent =
                javax.swing.SwingUtilities.getWindowAncestor(this.getView());

        GameOverDialog dialog = new GameOverDialog(parent, message);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private PieceType askPromotionType(Piece pawn) {
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
}
