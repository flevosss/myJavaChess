package view.Panels;

import controller.GameController;
import model.Game;
import model.PieceColour;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPanel extends JPanel {
    private final JButton offlineButton;
    private final JButton onlineButton;

    public MainPanel(int boardHeight, int boardWidth) {
        // Set the layout manager to BorderLayout to position elements
        setLayout(new BorderLayout());

        // Title label at the top
        JLabel titleLabel = new JLabel("Welcome to Chess", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setPreferredSize(new Dimension(boardWidth, 100));  // Title height, dynamic width
        add(titleLabel, BorderLayout.NORTH);

        // Centering the buttons inside a vertical BoxLayout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Vertical stacking of buttons

        // Offline Button
        offlineButton = new JButton("Play Offline");
        offlineButton.setFont(new Font("Arial", Font.PLAIN, 18));
        offlineButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        offlineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logic to start offline game
                startOfflineGame(boardHeight, boardWidth);  // Pass board size to the game view
            }
        });

        // Online Button
        onlineButton = new JButton("Play Online");
        onlineButton.setFont(new Font("Arial", Font.PLAIN, 18));
        onlineButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        onlineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logic to start online game
                startOnlineGame();
            }
        });

        // Add buttons to the button panel
        buttonPanel.add(offlineButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add spacing between buttons
        buttonPanel.add(onlineButton);

        // Set the preferred size of the button panel (same height as chessboard)
        buttonPanel.setPreferredSize(new Dimension(boardWidth / 3, boardHeight)); // Panel width set to 1/3 of board width

        // Add the button panel to the EAST of the main panel
        add(buttonPanel, BorderLayout.EAST); // Buttons will be centered on the right side of the panel
    }

    // Method to start offline game within the current window
    private void startOfflineGame(int boardHeight, int boardWidth) {
        // Create players and game
        Player white = new Player("White", PieceColour.WHITE);
        Player black = new Player("Black", PieceColour.BLACK);
        Game game = new Game(white, black);
        GameController controller = new GameController(game);

        // Create the game view (chessboard)
        JPanel gameView = controller.getView();

        // Set the preferred size of the game view (chessboard)
        // Set height to match the window height, and the width to take up most of the window (2/3 of boardWidth)
        gameView.setPreferredSize(new Dimension(boardWidth * 2 / 3, boardHeight));

        // Add the game view to the LEFT side of the panel (centered to fill up space)
        add(gameView, BorderLayout.CENTER);

        // Revalidate and repaint to update the layout dynamically
        revalidate();
        repaint();
    }

    // Method to start online game
    private void startOnlineGame() {
        System.out.println("Starting online game...");
        // Add logic to start online game
    }
}
