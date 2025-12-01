package network;

public class Protocol {

    public static String serverSendHello() {
        return "";
    }

    public static String sendErrorToClient(String message) {
        return "ERROR~" + message + "\n";
    }
}
