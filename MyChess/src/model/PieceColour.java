package model;

public enum PieceColour {
    BLACK, WHITE, EMPTY, NOT_ASSIGNED;

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
