package controller;

import model.*;
import view.GraphicsBoard;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameInputHandler extends MouseAdapter {

    private final Game game;
    private final GraphicsBoard graphicsBoard;

    public GameInputHandler(Game game, GraphicsBoard graphicsBoard) {
        this.game = game;
        this.graphicsBoard = graphicsBoard;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int col = e.getX() / graphicsBoard.getTileSize();
        int row = e.getY() / graphicsBoard.getTileSize();

        Piece selected = game.getBoard().getPiece(row, col);

        if (selected != null && selected.getType() != PieceType.EMPTY) {
            game.setSelectedPiece(selected);
            graphicsBoard.startDragging(selected, e.getX(), e.getY());
        } else {
            game.setSelectedPiece(null);
            graphicsBoard.stopDragging();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Piece selected = game.getSelectedPiece();
        if (selected == null || selected.getType() == PieceType.EMPTY) {
            return;
        }

        graphicsBoard.updateDragging(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Piece selected = game.getSelectedPiece();
        if (selected == null || selected.getType() == PieceType.EMPTY) {
            graphicsBoard.stopDragging();
            return;
        }

        graphicsBoard.stopDragging();

        int tile = graphicsBoard.getTileSize();
        int targetCol = e.getX() / tile;
        int targetRow = e.getY() / tile;

        Move move = new Move(
                selected.getRow(),
                selected.getColumn(),
                targetRow,
                targetCol
        );

        game.doMove(move);
        game.setSelectedPiece(null);
        graphicsBoard.repaint();
    }
}
