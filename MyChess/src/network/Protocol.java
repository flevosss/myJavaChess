package network;


import model.Move;

public class Protocol {

    public static String serverSendHello() {
        return "HELLO";
    }

    public static String sendErrorToClient(String message) {
        return "ERROR~" + message + "\n";
    }
    //MOVE~<fromRow>~~<fromCol>~~<toRow>~~<ToCol>
    public static String sendMove(Move move) {
        return "MOVE~" + move.getFromRow() + "~" + move.getFromCol() + "~" + move.getToRow() + "~" + move.getToCol();
    }

    public static String sendList() {
        return "LIST";
    }

    //CHALLENGE~<name>~<TIME>
    public static String sendChallengeTo(String name, int time) {
        return "CHALLENGE~" + name + "~" + time;
    }

    public static String sendLogin(String username) {
        return "LOGIN~" + username;
    }

    public static String sendQueue() {
        return "QUEUE";
    }

}
