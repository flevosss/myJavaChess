import model.*;
import model.Game;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameIntegrationTest {

    private Game newGame() {
        Player white = new Player("White", PieceColour.WHITE);
        Player black = new Player("Black", PieceColour.BLACK);
        return new Game(white, black);
    }

    @Test
    void testWhiteKingsideCastling() {
        Game game = newGame();
        Board board = game.getBoard();

        // 1. White: Ng1–f3  (7,6 -> 5,5)
        Piece knightG1 = board.getPiece(7, 6);
        game.doMove(new Move(7, 6, 5, 5, knightG1));

        // 1... Black: a7–a6  (1,0 -> 2,0)
        Piece pawnA7 = board.getPiece(1, 0);
        game.doMove(new Move(1, 0, 2, 0, pawnA7));

        // 2. White: Bf1–e2  (7,5 -> 6,4)
        Piece bishopF1 = board.getPiece(7, 5);
        game.doMove(new Move(7, 5, 6, 4, bishopF1));

        // 2... Black: a6–a5  (2,0 -> 3,0)
        pawnA7 = board.getPiece(2, 0);
        game.doMove(new Move(2, 0, 3, 0, pawnA7));

        // 3. White: 0-0  (Ke1–g1 = 7,4 -> 7,6)
        Piece kingE1 = board.getPiece(7, 4);
        game.doMove(new Move(7, 4, 7, 6, kingE1));

        // Assertions: king and rook must be on castled squares
        Piece king = board.getPiece(7, 6);
        assertEquals(PieceType.KING, king.getType());
        assertEquals(PieceColour.WHITE, king.getColour());

        // Rook from h1 (7,7) must be on f1 (7,5)
        Piece rook = board.getPiece(7, 5);
        assertEquals(PieceType.ROOK, rook.getType());
        assertEquals(PieceColour.WHITE, rook.getColour());

        // Original squares should now be empty
        assertEquals(PieceType.EMPTY, board.getPiece(7, 4).getType()); // e1
        assertEquals(PieceType.EMPTY, board.getPiece(7, 7).getType()); // h1
    }

    @Test
    void testEnPassantWhiteCapturesBlack() {
        Game game = newGame();
        Board board = game.getBoard();

        // 1. White: e2–e4  (6,4 -> 4,4)
        Piece wpE2 = board.getPiece(6, 4);
        game.doMove(new Move(6, 4, 4, 4, wpE2));

        // 1... Black: a7–a6  (1,0 -> 2,0)
        Piece bpA7 = board.getPiece(1, 0);
        game.doMove(new Move(1, 0, 2, 0, bpA7));

        // 2. White: e4–e5  (4,4 -> 3,4)
        wpE2 = board.getPiece(4, 4);
        game.doMove(new Move(4, 4, 3, 4, wpE2));

        // 2... Black: d7–d5  (1,3 -> 3,3) double step next to pawn
        Piece bpD7 = board.getPiece(1, 3);
        game.doMove(new Move(1, 3, 3, 3, bpD7));

        // 3. White: en passant e5xd6  (3,4 -> 2,3)
        wpE2 = board.getPiece(3, 4);
        game.doMove(new Move(3, 4, 2, 3, wpE2));

        // White pawn must be on d6
        Piece pawn = board.getPiece(2, 3);
        assertEquals(PieceType.PAWN, pawn.getType());
        assertEquals(PieceColour.WHITE, pawn.getColour());

        // Black pawn from d5 must have been removed (en passant capture)
        assertEquals(PieceType.EMPTY, board.getPiece(3, 3).getType());
    }

    @Test
    void testEnPassantMustBeImmediate() {
        Game game = newGame();
        Board board = game.getBoard();

        // Same setup as previous test until black plays d7–d5
        // 1. White: e2–e4
        Piece wpE2 = board.getPiece(6, 4);
        game.doMove(new Move(6, 4, 4, 4, wpE2));

        // 1... Black: a7–a6
        Piece bpA7 = board.getPiece(1, 0);
        game.doMove(new Move(1, 0, 2, 0, bpA7));

        // 2. White: e4–e5
        wpE2 = board.getPiece(4, 4);
        game.doMove(new Move(4, 4, 3, 4, wpE2));

        // 2... Black: d7–d5
        Piece bpD7 = board.getPiece(1, 3);
        game.doMove(new Move(1, 3, 3, 3, bpD7));

        // 3. White plays something else instead of en passant: Ng1–f3
        Piece knightG1 = board.getPiece(7, 6);
        game.doMove(new Move(7, 6, 5, 5, knightG1));

        // 3... Black random reply: a6–a5
        bpA7 = board.getPiece(2, 0);
        game.doMove(new Move(2, 0, 3, 0, bpA7));

        // 4. Now White tries en passant illegally: e5xd6
        // It should be rejected (wrong turn now – it's Black's turn)
        Piece wpE5 = board.getPiece(3, 4);
        game.doMove(new Move(3, 4, 2, 3, wpE5));

        // Pawn should still be on e5 (3,4)
        Piece whitePawnStill = board.getPiece(3, 4);
        assertEquals(PieceType.PAWN, whitePawnStill.getType());
        assertEquals(PieceColour.WHITE, whitePawnStill.getColour());

        // Black pawn should still be on d5 (3,3)
        Piece blackPawnStill = board.getPiece(3, 3);
        assertEquals(PieceType.PAWN, blackPawnStill.getType());
        assertEquals(PieceColour.BLACK, blackPawnStill.getColour());
    }

    @Test
    void testFoolsMateCheckmate() {
        Game game = newGame();
        Board board = game.getBoard();

        // 1. White: f2–f3  (6,5 -> 5,5)
        Piece wpF2 = board.getPiece(6, 5);
        game.doMove(new Move(6, 5, 5, 5, wpF2));

        // 1... Black: e7–e5  (1,4 -> 3,4)
        Piece bpE7 = board.getPiece(1, 4);
        game.doMove(new Move(1, 4, 3, 4, bpE7));

        // 2. White: g2–g4  (6,6 -> 4,6)
        Piece wpG2 = board.getPiece(6, 6);
        game.doMove(new Move(6, 6, 4, 6, wpG2));

        // 2... Black: Qd8–h4#  (0,3 -> 4,7)
        Piece bQd8 = board.getPiece(0, 3);
        game.doMove(new Move(0, 3, 4, 7, bQd8));

        // Now it should be checkmate: White to move, in check, no legal moves
        assertTrue(game.isGameOver(), "Game should be over after Fool's Mate");
        assertTrue(game.getValidMoves(PieceColour.WHITE).isEmpty(),
                "White should have no legal moves in checkmate position");
    }
}
