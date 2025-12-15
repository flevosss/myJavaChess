package controller;

import model.*;
import view.Panels.GraphicsBoard;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameInputHandler extends MouseAdapter {

    private final IGameController controller;
    private final GraphicsBoard graphicsBoard;

    private Piece selectedPiece;

    public GameInputHandler(IGameController controller, GraphicsBoard graphicsBoard) {
        this.controller = controller;
        this.graphicsBoard = graphicsBoard;
    }

    private Game getGame() {
        return controller.getGame();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point boardPt = graphicsBoard.screenToBoard(e.getX(), e.getY());
        int col = boardPt.x;
        int row = boardPt.y;

        // bounds safety
        if (row < 0 || row >= getGame().getBoard().getRows() ||
                col < 0 || col >= getGame().getBoard().getColumns()) {
            clearSelection();
            return;
        }

        Piece clicked = getGame().getBoard().getPiece(row, col);

        if (clicked != null
                && clicked.getType() != PieceType.EMPTY
                && getGame().isTurnForPiece(clicked)
                && controller.canSelectPiece(clicked)) {

            selectedPiece = clicked;
            calculateTargets(selectedPiece);
            graphicsBoard.startDragging(selectedPiece, e.getX(), e.getY());
        } else {
            clearSelection();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point boardPt = graphicsBoard.screenToBoard(e.getX(), e.getY());
        int col = boardPt.x;
        int row = boardPt.y;

        if (row < 0 || row >= getGame().getBoard().getRows() ||
                col < 0 || col >= getGame().getBoard().getColumns()) {
            clearSelection();
            return;
        }

        Piece clicked = getGame().getBoard().getPiece(row, col);

        if (clicked != null
                && clicked.getType() != PieceType.EMPTY
                && getGame().isTurnForPiece(clicked)
                && controller.canSelectPiece(clicked)) {

            selectedPiece = clicked;
            calculateTargets(selectedPiece);
        } else {
            clearSelection();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedPiece == null || selectedPiece.getType() == PieceType.EMPTY) return;
        graphicsBoard.updateDragging(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (selectedPiece == null || selectedPiece.getType() == PieceType.EMPTY) {
            graphicsBoard.stopDragging();
            return;
        }

        graphicsBoard.stopDragging();

        Point boardPt = graphicsBoard.screenToBoard(e.getX(), e.getY());
        int targetCol = boardPt.x;
        int targetRow = boardPt.y;

        // bounds safety
        if (targetRow < 0 || targetRow >= getGame().getBoard().getRows() ||
                targetCol < 0 || targetCol >= getGame().getBoard().getColumns()) {
            clearSelection();
            return;
        }

        Move move = new Move(
                selectedPiece.getRow(),
                selectedPiece.getColumn(),
                targetRow,
                targetCol
        );

        controller.handleMove(move);

        clearSelection();
    }

    private void calculateTargets(Piece selected) {
        List<Move> allMoves = getGame().getValidMoves(selected.getColour());
        List<Point> targets = new ArrayList<>();

        for (Move m : allMoves) {
            if (m.getFromRow() == selected.getRow()
                    && m.getFromCol() == selected.getColumn()) {
                // store as BOARD coordinates (col,row)
                targets.add(new Point(m.getToCol(), m.getToRow()));
            }
        }

        graphicsBoard.setHighlightSquares(targets);
    }

    private void clearSelection() {
        selectedPiece = null;
        graphicsBoard.stopDragging();
        graphicsBoard.clearHighlightSquares();
    }
}
