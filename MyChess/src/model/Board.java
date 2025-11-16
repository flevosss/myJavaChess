package model;

public class Board{

    public final Piece[][] pieces;
    private final int columns;
    private final int rows;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.columns = cols;
        this.pieces = new Piece[rows][cols];
        fillEmptyPieces();
        fillPawns();
        fillMilitary();
    }

    private void fillEmptyPieces() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Piece piece = new Piece(PieceType.EMPTY, PieceColour.EMPTY,i,j);
                setField(piece);
            }
        }
    }

    public int getColumns(){
        return columns;
    }


    public int getRows(){
        return rows;
    }


    public void setField(Piece piece) {
        pieces[piece.getRow()][piece.getColumn()] = piece;
    }

    public void removeField(int row, int column) {
        setField(new Piece(PieceType.EMPTY, PieceColour.EMPTY, row, column));
    }

    public Piece getPiece(int row, int column) {
        return pieces[row][column];
    }

    public boolean isInBounds(int row, int column){
        return row >= 0 && row < getRows()
                && column >= 0 && column < getColumns();
    }

    public boolean isEmptyField(int row, int column) {
        Piece pieceToCheck = getPiece(row,column);
        return pieceToCheck.getType().equals(PieceType.EMPTY);
    }

    private void fillMilitary() {
        //white
        setField(new Piece(PieceType.ROOK,  PieceColour.BLACK, 0, 0));
        setField(new Piece(PieceType.KNIGHT, PieceColour.BLACK, 0, 1));
        setField(new Piece(PieceType.BISHOP, PieceColour.BLACK, 0, 2));
        setField(new Piece(PieceType.QUEEN,  PieceColour.BLACK, 0, 3));
        setField(new Piece(PieceType.KING,   PieceColour.BLACK, 0, 4));
        setField(new Piece(PieceType.BISHOP, PieceColour.BLACK, 0, 5));
        setField(new Piece(PieceType.KNIGHT, PieceColour.BLACK, 0, 6));
        setField(new Piece(PieceType.ROOK,   PieceColour.BLACK, 0, 7));

        //black
        setField(new Piece(PieceType.ROOK,   PieceColour.WHITE, 7, 0));
        setField(new Piece(PieceType.KNIGHT, PieceColour.WHITE, 7, 1));
        setField(new Piece(PieceType.BISHOP, PieceColour.WHITE, 7, 2));
        setField(new Piece(PieceType.QUEEN,  PieceColour.WHITE, 7, 3));
        setField(new Piece(PieceType.KING,   PieceColour.WHITE, 7, 4));
        setField(new Piece(PieceType.BISHOP, PieceColour.WHITE, 7, 5));
        setField(new Piece(PieceType.KNIGHT, PieceColour.WHITE, 7, 6));
        setField(new Piece(PieceType.ROOK,   PieceColour.WHITE, 7, 7));
    }

    private void fillPawns(){
        for (int col = 0; col < 8; col++) {
            setField(new Piece(PieceType.PAWN, PieceColour.BLACK, 1, col));
        }

        for (int col = 0; col < 8; col++) {
            setField(new Piece(PieceType.PAWN, PieceColour.WHITE, 6, col));
        }
    }

    @Override
    public String toString(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.println(i + " " + j + " " +  pieces[i][j]);
            }
        }
        return null;
    }
}
