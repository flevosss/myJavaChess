package model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Board board;

    private final Player player1;
    private boolean hasBlackKingMoved;

    private final Player player2;
    private boolean hasWhiteKingMoved;

    private Player currentTurn;

    private final List<Move> movesPlayed;
    private final ChessRules chessRules;

    private boolean kingInCheck;
    public boolean isGameOver;

    public Game(Player player1, Player player2) {
        this.board = new Board(8,8);
        this.player1 = player1;
        this.player2 = player2;
        this.currentTurn = player1;

        this.isGameOver = false;
        this.kingInCheck = false;
        this.hasBlackKingMoved = false;
        this.hasWhiteKingMoved = false;

        this.chessRules = new ChessRules(this);
        this.movesPlayed = new ArrayList<>();
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

        if (pieceTobeMoved.getType() == PieceType.KING) {
            if (pieceTobeMoved.getColour() == PieceColour.WHITE) {
                hasWhiteKingMoved = true;
            } else if (pieceTobeMoved.getColour() == PieceColour.BLACK) {
                hasWhiteKingMoved = true;
            }
        }

        boolean isCastling = pieceTobeMoved.getType() == PieceType.KING
                && move.getFromRow() == move.getToRow()
                && Math.abs(move.getToCol() - move.getFromCol()) == 2;

        pieceTobeMoved.setRow(move.getToRow());
        pieceTobeMoved.setColumn(move.getToCol());
        board.setField(pieceTobeMoved);

        if (isCastling) {
            boolean kingSide = move.getToCol() > move.getFromCol();
            int rookFromCol = kingSide ? 7 : 0;
            int rookToCol = kingSide ? move.getToCol()  - 1 : move.getToCol()  + 1;

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

        if ((kingInCheck && !hasMove) || (!kingInCheck && !hasMove)) {
            isGameOver = true;
        }
    }

    public boolean needsPromotion(Piece piece) {
        if (piece == null) return false;
        if (piece.getType() != PieceType.PAWN) return false;

        PieceColour colour = piece.getColour();
        int row = piece.getRow();

        return (colour == PieceColour.WHITE && row == 0) ||
                (colour == PieceColour.BLACK && row == 7);
    }

    public void promotePawn(Piece pawn, PieceType newType) {
        Piece promoted = new Piece(newType, pawn.getColour(), pawn.getRow(), pawn.getColumn());
        board.setField(promoted);
    }

    public boolean isValidMove(Move move) {
        return chessRules.isValidMove(move);
    }

    public List<Move> getValidMoves(PieceColour colour) {
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
                        Move m = new Move(fromRow, fromCol, toRow, toCol);
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

    public boolean isKingInCheck() {
        return kingInCheck;
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

    public Board getBoard() {
        return this.board;
    }

    public boolean isGameOver() {
        return this.isGameOver;
    }

    public boolean hasBlackKingMoved () {
        return this.hasBlackKingMoved;
    }

    public boolean hasWhiteKingMoved() {
        return this.hasWhiteKingMoved;
    }
}