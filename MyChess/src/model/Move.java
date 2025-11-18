package model;

public class Move {
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;
    private final Piece piece;

    private boolean enPassant = false;

    public Move (int fromRow, int fromCol, int toRow, int toCol, Piece piece){
        this.fromCol = fromCol;
        this.fromRow = fromRow;
        this.toRow = toRow;
        this.toCol = toCol;
        this.piece = piece;
    }

    public Piece getPiece() {
        return this.piece;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getFromCol() {
        return fromCol;
    }

    public int getToCol() {
        return toCol;
    }

    public int getToRow() {
        return toRow;
    }

    public boolean isEnPassant() {
        return enPassant;
    }

    public void setEnPassant(boolean enPassant) {
        this.enPassant = enPassant;
    }
}
