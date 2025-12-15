package model;

public class ConnectionInfo {
    public String serverAddress;
    public int port;
    public String username;

    public ConnectionInfo(String serverAddress, int port, String username) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.username = username;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }
}