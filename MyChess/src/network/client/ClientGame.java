package network.client;


import model.Game;
import model.Player;

public class ClientGame {

    private final Game game;
    private final Client client;

    public ClientGame(Client client, Game game) {
        this.client = client;
        this.game = game;
    }


}
