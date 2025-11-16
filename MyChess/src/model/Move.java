package model;

public class Move {
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;

    public Move (int fromRow, int fromCol, int toRow, int toCol){
        this.fromCol = fromCol;
        this.fromRow = fromRow;
        this.toRow = toRow;
        this.toCol = toCol;
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
}
