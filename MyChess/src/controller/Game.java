package controller;

import model.*;
import view.GraphicsBoard;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Board board;
    private GameInputHandler gameInputHandler;
    private final GraphicsBoard view;

    private Piece selectedPiece;

    private Player currentTurn;
    private final Player player1;
    private final Player player2;

    private final List<Move> movesPlayed;
    private boolean kingInCheck;

    public boolean isGameOver;

    public Game(Player player1, Player player2){
        this.board = new Board(8,8);
        this.player1 = player1;
        this.player2 = player2;
        this.currentTurn = player1;

        this.isGameOver = false;
        this.kingInCheck = false;

        movesPlayed = new ArrayList<>();

        view = new GraphicsBoard(board,85);
        gameInputHandler = new GameInputHandler(this, view);

        view.addMouseListener(gameInputHandler);
        view.addMouseMotionListener(gameInputHandler);
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
                        if (isValidMove(m)) {
                            moves.add(m);
                        }
                    }
                }
            }
        }

        currentTurn = originalTurn;
        return moves;
    }

    public void doMove(Move move) {
        if (isGameOver) {
            System.out.println("Game is over");
            return;
        }
        //check valid etc, turn
        if (!isValidMove(move)){
            System.out.println("not a valid move");
            return;
        }
        Piece pieceTobeMoved = board.getPiece(move.getFromRow(), move.getFromCol());

        board.removeField(move.getFromRow(), move.getFromCol());
        pieceTobeMoved.setRow(move.getToRow());
        pieceTobeMoved.setColumn(move.getToCol());
        changeTurns();
        movesPlayed.add(move);

        board.setField(pieceTobeMoved);
        kingInCheck = isKingInCheck(currentTurn.getPieceColour());

        boolean hasMove = !getValidMoves(currentTurn.getPieceColour()).isEmpty();

        if (kingInCheck && !hasMove) {
            this.isGameOver = true;
            System.out.println("Checkmate! " + currentTurn + " has no legal moves.");
        } else if (!kingInCheck && !hasMove) {
            this.isGameOver = true;
            System.out.println("Stalemate! " + currentTurn + " has no legal moves.");
        }
        view.repaint();
    }

    private boolean isKingInCheck(PieceColour kingColour) {
        Piece king = board.getKing(kingColour);
        if (king == null) {
            System.out.println("No king found?");
            return false;
        }
        int kingRow = king.getRow();
        int kingCol = king.getColumn();

        PieceColour attackerColour = kingColour.getOtherColour(); //we check if the king is attacked by the other colour
        return isSquareAttacked(kingRow, kingCol, attackerColour);
    }

    private boolean isSquareAttacked(int targetRow, int targetCol, PieceColour byColour) {
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getColumns(); col++) {
                Piece p = board.getPiece(row, col);

                if (p.getType() == PieceType.EMPTY) continue;
                if (p.getColour() != byColour) continue;
                //fake the move
                Move pseudo = new Move(row, col, targetRow, targetCol, p);
                //and now check if any of these can land on my piece
                switch (p.getType()) {
                    case PAWN -> {
                        if (isValidForPawn(pseudo)) return true;
                    }
                    case KNIGHT -> {
                        if (isValidForKnight(pseudo)) return true;
                    }
                    case BISHOP -> {
                        if (isValidForBishop(pseudo)) return true;
                    }
                    case ROOK -> {
                        if (isValidForRook(pseudo)) return true;
                    }
                    case QUEEN -> {
                        if (isValidForQueen(pseudo)) return true;
                    }
                    case KING -> {
                        if (isValidForKing(pseudo)) return true;
                    }
                    default -> {}
                }
            }
        }
        return false;
    }

    /**
     * Checks if a king can attack a piece.
     * @param move the move the king wants to play.
     * @return true if it can, false otherwise.
     */
    private boolean isValidForKing(Move move) {
        int rowDifference = Math.abs(move.getToRow() - move.getFromRow());
        int columnDifference = Math.abs(move.getToCol() - move.getFromCol());

        //means that a king attacks any square in the 3x3 valid matrix next to him
        return (rowDifference <= 1 && columnDifference <= 1) &&
                !(rowDifference == 0 && columnDifference == 0); //without his piece ;)
    }

    private boolean isValidMove(Move move) {
        if (move.getFromRow() == move.getToRow() && move.getFromCol() == move.getToCol()) {
            System.out.println("piece must move");
            return false;
        }

        if (!board.isInBounds(move.getToRow(), move.getToCol())) {
            System.out.println("not in bounds");
            return false;
        }

        Piece pieceTobeMoved = board.getPiece(move.getFromRow(), move.getFromCol());
        if (pieceTobeMoved.getType() == PieceType.EMPTY){
            System.out.println("Nothing to move");
            return false;
        }

        PieceColour pieceColour = pieceTobeMoved.getColour();
        if  (currentTurn.getPieceColour() != pieceColour) {
            System.out.println("not your turn");
            return false;
        }

        PieceColour goToPiece = board.getPiece(move.getToRow(), move.getToCol()).getColour();
        if (pieceColour == goToPiece) {
            System.out.println("cant kill your teammate");
            return false;
        }

        boolean movementOk = false;
        switch (pieceTobeMoved.getType()) {
            case PAWN   -> movementOk = isValidForPawn(move);
            case KNIGHT -> movementOk = isValidForKnight(move);
            case BISHOP -> movementOk = isValidForBishop(move);
            case ROOK   -> movementOk = isValidForRook(move);
            case QUEEN  -> movementOk = isValidForQueen(move);
            case KING   -> movementOk = checkKing(move);
        }

        if (!movementOk) {
            return false;
        }

        //must not leave own king in check
        if (pieceTobeMoved.getType() != PieceType.KING) {
            if (leavesKingInCheckAfterMove(move, pieceTobeMoved)) {
                System.out.println("move leaves your king in check");
                return false;
            }
        }

        return true;
    }

    private boolean checkKing(Move move) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow   = move.getToRow();
        int toCol   = move.getToCol();

        int rowDifference = Math.abs(toRow - fromRow);
        int columnDifference = Math.abs(toCol - fromCol);

        //must move at least one and at most one square
        if (rowDifference == 0 && columnDifference == 0) {
            return false;
        }
        if (rowDifference > 1 || columnDifference > 1) {
            return false;
        }

        Piece king = board.getPiece(fromRow, fromCol);

        //king move is valid only if it does not leave king in check
        return !leavesKingInCheckAfterMove(move, king);
    }

    private boolean leavesKingInCheckAfterMove(Move move, Piece movingPiece) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow   = move.getToRow();
        int toCol   = move.getToCol();

        PieceColour colour = movingPiece.getColour();
        Piece captured = board.getPiece(toRow, toCol);

        //fake move
        board.removeField(fromRow, fromCol);
        board.removeField(toRow, toCol);

        movingPiece.setRow(toRow);
        movingPiece.setColumn(toCol);
        board.setField(movingPiece);

        boolean kingInCheck = isKingInCheck(colour);

        board.removeField(toRow, toCol);
        movingPiece.setRow(fromRow);
        movingPiece.setColumn(fromCol);
        board.setField(movingPiece);

        if (captured.getType() != PieceType.EMPTY) {
            board.setField(captured);
        }

        return kingInCheck;
    }

    private boolean isValidForQueen(Move move) {
        return isValidForRook(move) || isValidForBishop(move);
    }

    private boolean isValidForRook(Move move) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow   = move.getToRow();
        int toCol   = move.getToCol();

        //must be straight
        if (fromRow != toRow && fromCol != toCol) {
            return false;
        }

        int rowInc = Integer.compare(toRow, fromRow);
        int colInc = Integer.compare(toCol, fromCol);

        List<Piece> between = getInBetweenPieces(move, rowInc, colInc);
        for (Piece piece : between) {
            if (piece.getType() != PieceType.EMPTY) {
                return false;
            }
        }
        return true;
    }

    private List<Piece> getInBetweenPieces(Move move, int rowIncrement, int colIncrement) {
        List<Piece> result = new ArrayList<>();

        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow   = move.getToRow();
        int toCol   = move.getToCol();

        int currentRow = fromRow + rowIncrement;
        int currentCol = fromCol + colIncrement;

        while (currentRow != toRow || currentCol != toCol) {
            result.add(board.getPiece(currentRow, currentCol));
            currentRow += rowIncrement;
            currentCol += colIncrement;
        }
        return result;
    }

    private boolean isValidForBishop(Move move) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow   = move.getToRow();
        int toCol   = move.getToCol();

        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        if (Math.abs(rowDiff) != Math.abs(colDiff)) {
            return false; // not diagonal
        }

        int rowIncrement = (rowDiff > 0) ? 1 : -1;
        int columnIncrement = (colDiff > 0) ? 1 : -1;

        List<Piece> piecesInBetween = getInBetweenPieces(move, rowIncrement, columnIncrement);
        for (Piece piece : piecesInBetween) {
            if (piece.getType() != PieceType.EMPTY) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidForKnight(Move move) {
        int rowDifference = Math.abs(move.getToRow() - move.getFromRow());
        int columnDifference = Math.abs(move.getToCol() - move.getFromCol());

        return (rowDifference == 2 && columnDifference == 1) || (rowDifference == 1 && columnDifference == 2);
    }

    private boolean isFirstMoveForPawn(Piece piece){
        if (piece.getColour() == PieceColour.WHITE) {
            return piece.getRow() == 6;
        } else if (piece.getColour() == PieceColour.BLACK) {
            return piece.getRow() == 1;
        }
        return false;
    }

    private boolean isValidForPawn(Move move) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow   = move.getToRow();
        int toCol   = move.getToCol();

        Piece pawn = board.getPiece(move.getFromRow(), move.getFromCol());
        Piece target = board.getPiece(toRow, toCol);

        int directionRow = toRow - fromRow;
        int directionCol = toCol - fromCol;

        int direction = pawn.getColour() == PieceColour.WHITE ? -1 : 1; //white up so decreasing

        if (directionCol == 0 && directionRow == 0) {
            return false; //not  a move
        }

        //check straight move
        if (directionCol == 0) {
            if (target.getType() != PieceType.EMPTY) {
                return false; //cant capture on straight
            }
            if (direction * directionRow < 0) { //this means we are going back
                return false;
            }

            if(Math.abs(directionRow) == 2) {
                //dont hop on another pawn :D
                if (board.getPiece(fromRow + direction, fromCol).getType() != PieceType.EMPTY) {
                    return false;
                }
            }

            if (isFirstMoveForPawn(pawn)) {
                return Math.abs(directionRow) > 0 && Math.abs(directionRow) <= 2;
            } else {
                return Math.abs(directionRow) == 1;
            }

        }

        //check diagonal move
        if (Math.abs(directionCol) == 1 && directionRow == direction) {
            return target.getColour().equals(pawn.getColour().getOtherColour());
        }

        return false;
    }

    private void changeTurns() {
        if (currentTurn == player1) {
            System.out.println("changed turn to" + player2);
            currentTurn = player2;
        } else {
            System.out.println("changed turn to" + player1);
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
