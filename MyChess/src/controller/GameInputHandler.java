package controller;

import model.*;
import view.GraphicsBoard;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

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

        if (selected != null && selected.getType() != PieceType.EMPTY && game.isTurnForPiece(selected)) {
            calculateTargets(selected);

            game.setSelectedPiece(selected);
            graphicsBoard.startDragging(selected, e.getX(), e.getY());
        } else {
            game.setSelectedPiece(null);
            graphicsBoard.stopDragging();
        }
    }

    private void calculateTargets(Piece selected) {
        List<Move> allMoves = game.getValidMoves(selected.getColour());
        List<Point> targets = new ArrayList<>();

        for (Move m : allMoves) {
            if (m.getFromRow() == selected.getRow()
                    && m.getFromCol() == selected.getColumn()) {
                targets.add(new Point(m.getToCol(), m.getToRow()));
            }
        }

        graphicsBoard.setHighlightSquares(targets);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int col = e.getX() / graphicsBoard.getTileSize();
        int row = e.getY() / graphicsBoard.getTileSize();

        Piece selected = game.getBoard().getPiece(row, col);

        if (selected != null && selected.getType() != PieceType.EMPTY && game.isTurnForPiece(selected)) {
            game.setSelectedPiece(selected);

            calculateTargets(selected);
        } else {
            //if its empty square clear
            game.setSelectedPiece(null);
            graphicsBoard.clearHighlightSquares();
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
                targetCol,
                selected
        );

        game.doMove(move);
        game.setSelectedPiece(null);
        graphicsBoard.clearHighlightSquares();
        graphicsBoard.repaint();
    }
}
