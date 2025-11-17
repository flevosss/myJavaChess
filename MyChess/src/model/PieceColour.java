package model;

public enum PieceColour {
    BLACK, WHITE, EMPTY;

    public PieceColour getOtherColour() {
        if (this == BLACK) {
            return PieceColour.WHITE;
        }
        if (this == WHITE) {
            return PieceColour.BLACK;
        }
        return PieceColour.EMPTY;
    }
}
