package model;

import controller.Game;

public class Player {

    private final String name;
    private final PieceColour pieceColour;
    private Game game;

    public Player(String name, PieceColour pieceColour) {
        this.name = name;
        this.pieceColour = pieceColour;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Move doMove(Game game){
        return null;
    }

    public String getName() {
        return name;
    }

    public PieceColour getPieceColour() {
        return pieceColour;
    }

    public Game getGame() {
        return game;
    }
}
