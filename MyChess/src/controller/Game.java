package controller;

import model.*;
import view.GraphicsBoard;
import view.PromotionDialog;

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

    public Game(Player player1, Player player2){
        this.board = new Board(8,8);
        this.player1 = player1;
        this.player2 = player2;
        this.currentTurn = player1;

        this.isGameOver = false;
        this.kingInCheck = false;

        movesPlayed = new ArrayList<>();

        view = new GraphicsBoard(board,85);
        GameInputHandler gameInputHandler = new GameInputHandler(this, view);

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

        if (!isValidMove(move)){
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

    private boolean hasKingMoved(PieceColour colour) {
        for (Move m : movesPlayed) {
            Piece p = m.getPiece();
            if (p.getType() == PieceType.KING && p.getColour() == colour) {
                return true;
            }
        }
        return false;
    }

    private void handlePawnPromotion(Piece pawn) {
        if (pawn.getType() != PieceType.PAWN) {
            return;
        }

        int row = pawn.getRow();
        PieceColour colour = pawn.getColour();

        //white pawns promote on row 0, black pawns on row 7
        boolean promote =
                (colour == PieceColour.WHITE && row == 0) ||
                        (colour == PieceColour.BLACK && row == 7);

        if (!promote) {
            return;
        }

        int col = pawn.getColumn();
        PieceType promotedType = askPromotionType(pawn);

        Piece promoted = new Piece(promotedType, colour, row, col);
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

    private boolean hasRookMoved(PieceColour colour, int startRow, int startCol) {
        for (Move m : movesPlayed) {
            Piece p = m.getPiece();
            if (p.getType() == PieceType.ROOK && p.getColour() == colour) {
                if (m.getFromRow() == startRow && m.getFromCol() == startCol) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canCastle(Move move, Piece king) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toCol   = move.getToCol();

        PieceColour colour = king.getColour();

        //king must be on its home rank
        int homeRow = (colour == PieceColour.WHITE) ? 7 : 0;
        if (fromRow != homeRow) {
            return false;
        }

        if (fromCol != 4) {
            return false;
        }

        //king and rook must not have moved before
        if (hasKingMoved(colour)) {
            return false;
        }

        boolean kingSide = toCol > fromCol; // true = king-side, false = queen-side

        int rookStartCol = kingSide ? 7 : 0;
        int rookRow      = homeRow;

        Piece rook = board.getPiece(rookRow, rookStartCol);
        if (rook.getType() != PieceType.ROOK || rook.getColour() != colour) {
            return false;
        }

        if (hasRookMoved(colour, rookRow, rookStartCol)) {
            return false;
        }

        // squares between king and rook must be empty
        int step = (rookStartCol > fromCol) ? 1 : -1;
        for (int c = fromCol + step; c != rookStartCol; c += step) {
            if (board.getPiece(fromRow, c).getType() != PieceType.EMPTY) {
                return false;
            }
        }

        // king may not be in check now
        if (isKingInCheck(colour)) {
            return false;
        }

        PieceColour attacker = colour.getOtherColour();
        int kingTargetCol = toCol;
        int currentCol = fromCol;

        step = (kingTargetCol > fromCol) ? 1 : -1;
        currentCol += step;
        while (true) {
            if (isSquareAttacked(fromRow, currentCol, attacker)) {
                return false;
            }
            if (currentCol == kingTargetCol) {
                break;
            }
            currentCol += step;
        }

        return true;
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
     * Checks if the given pawn move is an en passant capture, based on the last move.
     */
    private boolean isEnPassantMove(Move move, Piece pawn) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow   = move.getToRow();
        int toCol   = move.getToCol();

        if (pawn.getType() != PieceType.PAWN) return false;
        if (movesPlayed.isEmpty()) return false;

        //target square must be empty
        if (board.getPiece(toRow, toCol).getType() != PieceType.EMPTY) return false;

        int direction = pawn.getColour() == PieceColour.WHITE ? -1 : 1;
        int directionRow = toRow - fromRow;
        int directionCol = toCol - fromCol;

        //our pawn moves one step diagonally forward
        if (!(Math.abs(directionCol) == 1 && directionRow == direction)) {
            return false;
        }
        Move last = movesPlayed.getLast();
        Piece lastPiece = last.getPiece();

        //last move must be enemy pawn double step
        if (lastPiece.getType() != PieceType.PAWN) return false;
        if (lastPiece.getColour() == pawn.getColour()) return false;
        if (Math.abs(last.getFromRow() - last.getToRow()) != 2) return false;

        if (last.getToRow() != fromRow) return false;
        return last.getToCol() == toCol;
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

        Piece king = board.getPiece(fromRow, fromCol);

        // castling: king moves two squares horizontally on same rank
        if (rowDifference == 0 && columnDifference == 2) {
            return canCastle(move, king);
        }

        //must move at least one and at most one square
        if (rowDifference == 0 && columnDifference == 0) {
            return false;
        }
        if (rowDifference > 1 || columnDifference > 1) {
            return false;
        }

        //king move is valid only if it does not leave king in check
        return !leavesKingInCheckAfterMove(move, king);
    }

    private boolean leavesKingInCheckAfterMove(Move move, Piece movingPiece) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow   = move.getToRow();
        int toCol   = move.getToCol();

        PieceColour colour = movingPiece.getColour();

        int capturedRow;
        int capturedCol;

        if (move.isEnPassant()) {
            capturedRow = fromRow;  // enemy pawn is on our original rank
        } else {
            capturedRow = toRow;
        }
        capturedCol = toCol;


        Piece captured = board.getPiece(capturedRow, capturedCol);

        //fake move
        board.removeField(fromRow, fromCol);
        if (captured.getType() != PieceType.EMPTY) {
            board.removeField(capturedRow, capturedCol);
        }

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
        int toRow = move.getToRow();
        int toCol = move.getToCol();

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

            if (Math.abs(directionRow) == 2) {
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
            if (target.getType() != PieceType.EMPTY) {
                return target.getColour().equals(pawn.getColour().getOtherColour());
            } else {
                if (isEnPassantMove(move, pawn)) {
                    move.setEnPassant(true);
                    return true;
                }
                return false;
            }
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
