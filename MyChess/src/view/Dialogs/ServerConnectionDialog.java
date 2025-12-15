package view.Dialogs;

import javax.swing.*;
import model.ConnectionInfo;

import java.awt.*;

/**
 * Dialog for connecting to the online chess server.
 * Handles user input for server address, port, and username.
 */
public class ServerConnectionDialog {

    /**
     * Shows the connection dialog and returns the user's input.
     * Returns null if the user cancels.
     */
    public static ConnectionInfo showDialog(JPanel parent, ConnectionInfo previousInfo) {
        JTextField serverField = new JTextField(previousInfo != null ? previousInfo.getServerAddress() : "");
        JTextField portField = new JTextField(previousInfo != null ? String.valueOf(previousInfo.getPort()) : "");
        JTextField userField = new JTextField(previousInfo != null ? previousInfo.getUsername() : "");

        JPanel connectionPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        connectionPanel.add(new JLabel("Server Address:"));
        connectionPanel.add(serverField);
        connectionPanel.add(new JLabel("Port:"));
        connectionPanel.add(portField);
        connectionPanel.add(new JLabel("Username:"));
        connectionPanel.add(userField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(
                    parent,
                    connectionPanel,
                    "Connect to Server",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return null; //user cancelled
            }

            String server = serverField.getText().trim();
            String portText = portField.getText().trim();
            String username = userField.getText().trim();

            if (server.isEmpty()) {
                JOptionPane.showMessageDialog(
                        parent, "Please enter a server address!", "Error", JOptionPane.ERROR_MESSAGE
                        );
                serverField.requestFocusInWindow();
                continue;
            }

            if (portText.isEmpty()) {
                JOptionPane.showMessageDialog(
                        parent, "Please enter a port!", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            int port;
            try {
                port = Integer.parseInt(portText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        parent, "Invalid port number!", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(
                        parent, "Please enter a username!", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            return new ConnectionInfo(server, port, username);
        }
    }

    /**
     * Shows an error dialog for connection failures.
     */
    public static void showError(JPanel parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
