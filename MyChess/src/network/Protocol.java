package network;


import model.Move;

public class Protocol {

    // Server to Client messages
    public static String serverSendHello() {
        return "HELLO";
    }

    public static String sendErrorToClient(String message) {
        return "ERROR~" + message;
    }
    
    // NEWGAME~<whitePlayerName>~<blackPlayerName>
    public static String sendNewGame(String whitePlayerName, String blackPlayerName) {
        return "NEWGAME~" + whitePlayerName + "~" + blackPlayerName;
    }
    
    // GAMEOVER~<winner>
    public static String sendGameOver(String winner) {
        return "GAMEOVER~" + winner;
    }
    
    // LOGIN~<username>
    public static String sendLogin(String username) {
        return "LOGIN~" + username;
    }

    // QUEUE
    public static String sendQueue() {
        return "QUEUE";
    }

    // LIST
    public static String sendList() {
        return "LIST";
    }

    // CHALLENGE~<name>~<TIME>
    public static String sendChallengeTo(String name, int time) {
        return "CHALLENGE~" + name + "~" + time;
    }
    
    // MOVE~<fromRow>~<fromCol>~<toRow>~<toCol>
    public static String sendMove(Move move) {
        return "MOVE~" + move.getFromRow() + "~" + move.getFromCol() + "~" + move.getToRow() + "~" + move.getToCol();
    }

}
