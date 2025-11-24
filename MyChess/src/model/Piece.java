package model;

public class Piece {
    private final PieceType type;
    private final PieceColour colour;
    private int row;
    private int column;

    public Piece(PieceType type, PieceColour colour, int row, int column) {
        this.type = type;
        this.colour = colour;
        this.row = row;
        this.column = column;
    }

    public void setRow(int row){
        this.row = row;
    }

    public void setColumn(int column){
        this.column = column;
    }

    public PieceType getType() {
        return type;
    }

    public PieceColour getColour() {
        return colour;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() { 
        return column;
    }

    @Override
    public String toString() {
        return "Piece{" +
                "type=" + type +
                ", colour=" + colour +
                ", row=" + row +
                ", column=" + column +
                '}';
    }
}
