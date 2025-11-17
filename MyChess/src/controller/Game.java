package controller;

import model.*;
import view.GraphicsBoard;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Board board;
    private GameInputHandler gameInputHandler;
    private final GraphicsBoard view;

    private Piece selectedPiece;

    private Player currentTurn;
    private Player player1;
    private Player player2;

    private List<Move> movesPlayed;

    public Game(Player player1, Player player2){
        this.board = new Board(8,8);
        this.player1 = player1;
        this.player2 = player2;
        this.currentTurn = player1;

        movesPlayed = new ArrayList<>();

        view = new GraphicsBoard(board,85);
        gameInputHandler = new GameInputHandler(this, view);

        view.addMouseListener(gameInputHandler);
        view.addMouseMotionListener(gameInputHandler);
    }

    public void doMove(Move move) {
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

        view.repaint();
    }

    private boolean isValidMove(Move move) {
        Piece pieceTobeMoved = board.getPiece(move.getFromRow(), move.getFromCol());
        if (pieceTobeMoved.getType() == PieceType.EMPTY){
            System.out.println("Nothing to move");
            return false;
        }
        //first identify the piece
        //also check that he doesnt move the other player in online, but maybe this has to be done in the clienthandler!
        PieceColour pieceColour = board.getPiece(move.getFromRow(), move.getFromCol()).getColour();
        if  (currentTurn.getPieceColour() != pieceColour) {
            System.out.println("not your turn");
            return false;
        }
        //check if its your teammate
        PieceColour goToPiece = board.getPiece(move.getToRow(), move.getToCol()).getColour();
        if (pieceColour == goToPiece) {
            System.out.println("cant kill your teammate");
            return false;
        }
        boolean result = false;
        switch (pieceTobeMoved.getType()) {
            case PAWN -> {
                result = checkPawn(move);

            }
            case KNIGHT ->
                result = true;

        }
        return result;
    }

    private boolean isFirstMoveForPawn(Piece piece){
        if (piece.getColour() == PieceColour.WHITE) {
            return piece.getRow() == 6;
        } else if (piece.getColour() == PieceColour.BLACK) {
            return piece.getRow() == 1;
        }
        return false;
    }

    private boolean checkPawn(Move move) {
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
}
