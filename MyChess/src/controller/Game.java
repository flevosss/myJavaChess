package controller;

import exception.InvalidMoveException;
import exception.NotYourMoveException;
import model.*;
import view.GraphicsBoard;

import java.util.List;

public class Game {
    private Board board;
    private GameInputHandler gameInputHandler;
    private final GraphicsBoard view;

    private Piece selectedPiece;

    private Player currentTurn;
    private Player player1;
    private Player player2;

    public Game(Player player1, Player player2){ //todo: player1 should always be white, and player2 black
        this.board = new Board(8,8);
        this.player1 = player1;
        this.player2 = player2;
        this.currentTurn = player1;

        view = new GraphicsBoard(board,85);
        gameInputHandler = new GameInputHandler(this, view);

        view.addMouseListener(gameInputHandler);
        view.addMouseMotionListener(gameInputHandler);
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

    public List<Move> getValidMoves(PieceType pieceType, PieceColour pieceColour){
        return null;
    }

    public List<Move> getValidMoves(Player player) {
        //get all of the pieces on board first

        Move moveToCheck = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //moveToCheck = new Move()
            }
        }
        return null;
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

    private void changeTurns() {
        if (currentTurn == player1) {
            currentTurn = player2;
        } else {
            currentTurn = player1;
        }
    }

    public void doMove(Move move) {
        //check valid etc, turn
        if (!isValidForPiece(move)){
            System.out.println("not a valid move");
            return;
        }

        Piece pieceTobeMoved = board.getPiece(move.getFromRow(), move.getFromCol());
        if (pieceTobeMoved.getType() == PieceType.EMPTY){
            System.out.println("Nothing to move");
            return;
        }
        board.removeField(move.getFromRow(), move.getFromCol());
        pieceTobeMoved.setRow(move.getToRow());
        pieceTobeMoved.setColumn(move.getToCol());
        changeTurns();

        board.setField(pieceTobeMoved);

        view.repaint();
    }

    private boolean isKingChecked(Player player, Move move){
        return false;
    }

    private boolean isValidForPiece(Move move) {
        //first identify the piece
        //also check that he doesnt move the other player in online, but maybe this has to be done in the clienthandler!
        PieceColour pieceColour = board.getPiece(move.getFromRow(), move.getFromCol()).getColour();
        if  (currentTurn.getPieceColour() != pieceColour) {
            System.out.println("not your turn");
            return false;
        }
        //check if its his piece

        return true;
    }

    private boolean isValidMove(Player player, Move move){
        if (!board.isInBounds(move.getToRow(), move.getToCol())) {
            throw new InvalidMoveException("Move is out of bounds!");
        }
        if (currentTurn != player) {
            throw new NotYourMoveException("It is not your move!");
        }
        if (isKingChecked(player, move)) {
            throw new InvalidMoveException("This protects the king from checkmate!");
        }
//        if (!isValidForPiece(move)) {
//            throw new InvalidMoveException("This move is not valid for this piece!");
//        }




        return true;
    }
}
