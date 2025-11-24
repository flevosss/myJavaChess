package controller;

import model.*;
import view.Dialogs.GameOverDialog;
import view.GraphicsBoard;
import view.Dialogs.PromotionDialog;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Board board;
    private final GraphicsBoard view;

    private Piece selectedPiece;

    private Player currentTurn;
    private final Player player1;
    private final Player player2;
    private final List<Move> movesPlayed;
    private boolean kingInCheck;
    public boolean isGameOver;
    private ChessRules chessRules;

    public Game(Player player1, Player player2){
        this.board = new Board(8,8);
        this.player1 = player1;
        this.player2 = player2;
        this.currentTurn = player1;

        this.isGameOver = false;
        this.kingInCheck = false;
        this.chessRules = new ChessRules(this);

        movesPlayed = new ArrayList<>();

        view = new GraphicsBoard(board,85);
        GameInputHandler gameInputHandler = new GameInputHandler(this, view);

        view.addMouseListener(gameInputHandler);
        view.addMouseMotionListener(gameInputHandler);
    }

    public void doMove(Move move) {
        if (isGameOver) {
            System.out.println("Game is over");
            return;
        }

        if (!chessRules.isValidMove(move)){
            System.out.println("not a valid move");
            return;
        }
        Piece pieceTobeMoved = board.getPiece(move.getFromRow(), move.getFromCol());

        board.removeField(move.getFromRow(), move.getFromCol());

        if (move.isEnPassant()) {
            int capturedRow = move.getFromRow();
            int capturedCol = move.getToCol();
            board.removeField(capturedRow, capturedCol);
        }

        boolean isCastling = pieceTobeMoved.getType() == PieceType.KING
                && move.getFromRow() == move.getToRow()
                && Math.abs(move.getToCol() - move.getFromCol()) == 2;


        pieceTobeMoved.setRow(move.getToRow());
        pieceTobeMoved.setColumn(move.getToCol());
        board.setField(pieceTobeMoved);

        handlePawnPromotion(pieceTobeMoved);

        if (isCastling) {
            boolean kingSide = move.getToCol() > move.getFromCol();
            int rookFromCol  = kingSide ? 7 : 0;
            int rookToCol    = kingSide ? move.getToCol()  - 1 : move.getToCol()  + 1;

            Piece rook = board.getPiece(move.getToRow(), rookFromCol);
            board.removeField(move.getToRow(), rookFromCol);
            rook.setRow(move.getToRow());
            rook.setColumn(rookToCol);
            board.setField(rook);
        }

        movesPlayed.add(move);
        changeTurns();

        kingInCheck = chessRules.isKingInCheck(currentTurn.getPieceColour());

        boolean hasMove = !getValidMoves(currentTurn.getPieceColour()).isEmpty();

        if (kingInCheck && !hasMove) {
            this.isGameOver = true;
            String msg = "Checkmate!\n" + currentTurn + " has no legal moves.";
            showGameOverDialog(msg);
        } else if (!kingInCheck && !hasMove) {
            this.isGameOver = true;
            String msg = "Stalemate!\n" + currentTurn + " has no legal moves.";
            showGameOverDialog(msg);
        }
        view.repaint();
    }

    public List<Move> getValidMoves(PieceColour colour){
        Player originalTurn = currentTurn;
        currentTurn = getPlayerFromPiece(colour);

        List<Move> moves = new ArrayList<>();

        for (int fromRow = 0; fromRow < board.getRows(); fromRow++) {
            for (int fromCol = 0; fromCol < board.getColumns(); fromCol++) {
                Piece p = board.getPiece(fromRow, fromCol);
                if (p.getType() == PieceType.EMPTY) continue;
                if (p.getColour() != colour) continue;

                for (int toRow = 0; toRow < board.getRows(); toRow++) {
                    for (int toCol = 0; toCol < board.getColumns(); toCol++) {
                        Move m = new Move(fromRow, fromCol, toRow, toCol, p);
                        if (chessRules.isValidMove(m)) {
                            moves.add(m);
                        }
                    }
                }
            }
        }

        currentTurn = originalTurn;
        return moves;
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

        int col = pawn.getColumn();
        PieceType promotedType = askPromotionType(pawn);

        Piece promoted = new Piece(promotedType, colour, pawnRow, col);
        board.setField(promoted);
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

            dialog.setLocation(pawnX, pawnY); //place the dialog on top of that pawn
        } catch (IllegalComponentStateException ex) {
            dialog.setLocationRelativeTo(parentWindow);
        }

        return dialog.selectPieceType();
    }

    public boolean isTurnForPiece(Piece piece) {
        return currentTurn.getPieceColour() == piece.getColour();
    }

    public List<Move> getMovesPlayed() {
        return this.movesPlayed;
    }

    public Player getCurrentTurn () {
        return this.currentTurn;
    }

    public void showGameOverDialog(String message) {
        java.awt.Window parent =
                javax.swing.SwingUtilities.getWindowAncestor(this.getView());

        GameOverDialog dialog = new GameOverDialog(parent, message);
        dialog.setLocationRelativeTo(parent); //center on window
        dialog.setVisible(true);
    }

    private void changeTurns() {
        if (currentTurn == player1) {
            currentTurn = player2;
        } else {
            currentTurn = player1;
        }
    }

    public Player getPlayerFromPiece(PieceColour colour) {
        if (colour.equals(PieceColour.WHITE)) {
            return player1;
        }
        if (colour.equals(PieceColour.BLACK)) {
            return player2;
        }
        return null;
    }

    public Board getBoard() {
        return this.board;
    }

    public GraphicsBoard getView(){
        return this.view;
    }

    public void setSelectedPiece(Piece piece) {
        this.selectedPiece = piece;
    }

    public Piece getSelectedPiece(){
        return this.selectedPiece;
    }

    public boolean isGameOver() {
        return this.isGameOver;
    }
}
