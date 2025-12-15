package controller;

import model.*;
import view.Panels.GraphicsBoard;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles mouse input for the chess board, managing piece selection, dragging, and click-to-move.
 */
public class GameInputHandler extends MouseAdapter {

    private final IGameController controller;
    private final GraphicsBoard graphicsBoard;

    private Piece selectedPiece;

    private Map<Integer, List<Move>> turnMoveMap;
    private List<Move> selectedMoves;

    public GameInputHandler(IGameController controller, GraphicsBoard graphicsBoard) {
        this.selectedMoves = new ArrayList<>();
        this.controller = controller;
        this.graphicsBoard = graphicsBoard;
        this.turnMoveMap = getGame().getCachedValidMoves();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point boardPoint = graphicsBoard.screenToBoard(e.getX(), e.getY());
        int col = boardPoint.x;
        int row = boardPoint.y;

        if (isOutOfBounds(row, col)) {
            clearSelection();
            return;
        }

        refreshTurnMoveMap();

        Piece clicked = getGame().getBoard().getPiece(row, col);

        if (isSelectablePiece(clicked)) {
            selectPiece(row, col);
            graphicsBoard.startDragging(selectedPiece, e.getX(), e.getY());
        } else {
            if (selectedPiece == null) {
                clearSelection();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point boardPoint = graphicsBoard.screenToBoard(e.getX(), e.getY());
        int col = boardPoint.x;
        int row = boardPoint.y;

        if (isOutOfBounds(row, col)) {
            clearSelection();
            return;
        }

        refreshTurnMoveMap();

        if (selectedPiece != null && selectedPiece.getType() != PieceType.EMPTY) {
            Move m = findSelectedMoveTo(row, col);
            if (m != null) {
                controller.handleMove(m);
                clearSelection();
                return;
            }
        }

        Piece clicked = getGame().getBoard().getPiece(row, col);

        if (isSelectablePiece(clicked)) {
            selectPiece(row, col);
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

        if (isOutOfBounds(targetRow, targetCol)) {
            clearSelection();
            return;
        }

        Move move = findSelectedMoveTo(targetRow, targetCol);
        if (move != null) {
            controller.handleMove(move);
        }

        clearSelection();
    }

    /**
     * Highlights all valid target squares for the selected piece.
     */
    private void highlightFromSelectedMoves() {
        List<Point> targets = new ArrayList<>();
        for (Move m : selectedMoves) {
            targets.add(new Point(m.getToCol(), m.getToRow()));
        }
        graphicsBoard.setHighlightSquares(targets);
    }

    /**
     * Finds a valid move from the selected piece to the specified target position.
     */
    private Move findSelectedMoveTo(int toRow, int toCol) {
        for (Move m : selectedMoves) {
            if (m.getToRow() == toRow && m.getToCol() == toCol) return m;
        }
        return null;
    }

    /**
     * Checks if the given position is outside the board boundaries.
     */
    private boolean isOutOfBounds(int row, int col) {
        return row < 0 || row >= getGame().getBoard().getRows() ||
                col < 0 || col >= getGame().getBoard().getColumns();
    }

    /**
     * Checks if the piece can be selected by the current player.
     */
    private boolean isSelectablePiece(Piece piece) {
        return piece != null &&
                piece.getType() != PieceType.EMPTY &&
                getGame().isTurnForPiece(piece) &&
                controller.canSelectPiece(piece);
    }

    /**
     * Selects a piece at the given position and highlights its valid moves.
     */
    private void selectPiece(int row, int col) {
        selectedPiece = getGame().getBoard().getPiece(row, col);

        List<Move> movesForPiece = turnMoveMap.get(keyOf(row, col));
        selectedMoves = movesForPiece != null ? new ArrayList<>(movesForPiece) : new ArrayList<>();

        graphicsBoard.setSelectedSquare(new Point(col, row));
        highlightFromSelectedMoves();
    }

    /**
     * Clears the current piece selection and all highlights.
     */
    private void clearSelection() {
        selectedPiece = null;
        selectedMoves.clear();
        graphicsBoard.stopDragging();
        graphicsBoard.clearHighlightSquares();
        graphicsBoard.clearSelectedSquare();
    }

    private Game getGame() {
        return controller.getGame();
    }

    private int keyOf(int row, int col) {
        return getGame().keyOf(row, col);
    }

    /**
     * Refreshes the cached valid moves for the current turn.
     */
    private void refreshTurnMoveMap() {
        turnMoveMap = getGame().getCachedValidMoves();
    }
}
