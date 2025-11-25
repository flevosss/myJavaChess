package controller;

import model.*;
import view.GraphicsBoard;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameInputHandler extends MouseAdapter {

    private final GameController controller;
    private final GraphicsBoard graphicsBoard;

    private Piece selectedPiece;

    public GameInputHandler(GameController controller, GraphicsBoard graphicsBoard) {
        this.controller = controller;
        this.graphicsBoard = graphicsBoard;
    }

    private Game getGame() {
        return controller.getGame();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int col = e.getX() / graphicsBoard.getTileSize();
        int row = e.getY() / graphicsBoard.getTileSize();

        Piece clicked = getGame().getBoard().getPiece(row, col);

        if (clicked != null && clicked.getType() != PieceType.EMPTY && getGame().isTurnForPiece(clicked)) {
            selectedPiece = clicked;

            calculateTargets(selectedPiece);
            graphicsBoard.startDragging(selectedPiece, e.getX(), e.getY());
        } else {
            selectedPiece = null;
            graphicsBoard.stopDragging();
            graphicsBoard.clearHighlightSquares();
        }
    }

    private void calculateTargets(Piece selected) {
        List<Move> allMoves = getGame().getValidMoves(selected.getColour());
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

        Piece selected = getGame().getBoard().getPiece(row, col);

        if (selected != null && selected.getType() != PieceType.EMPTY && getGame().isTurnForPiece(selected)) {
            selectedPiece = selected;
            calculateTargets(selectedPiece);
        } else {
            //if its empty square clear
            selectedPiece = null;
            graphicsBoard.clearHighlightSquares();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedPiece == null || selectedPiece.getType() == PieceType.EMPTY) {
            return;
        }

        graphicsBoard.updateDragging(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (selectedPiece == null || selectedPiece.getType() == PieceType.EMPTY) {
            graphicsBoard.stopDragging();
            return;
        }

        graphicsBoard.stopDragging();

        int tile = graphicsBoard.getTileSize();
        int targetCol = e.getX() / tile;
        int targetRow = e.getY() / tile;

        Move move = new Move(
                selectedPiece.getRow(),
                selectedPiece.getColumn(),
                targetRow,
                targetCol,
                selectedPiece
        );

        controller.handleMove(move);

        selectedPiece = null;
        graphicsBoard.clearHighlightSquares();
    }
}
