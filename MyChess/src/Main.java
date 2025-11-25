import controller.GameController;
import model.PieceColour;
import model.Player;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            Player white = new Player("White", PieceColour.WHITE);
            Player black = new Player("Black", PieceColour.BLACK);

            GameController controller = new GameController(white, black);

            JFrame window = new JFrame("Chess");
            window.setResizable(false);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            window.add(controller.getView());
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
}
