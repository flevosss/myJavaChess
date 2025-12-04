import controller.GameController;
import model.Game;
import model.PieceColour;
import model.Player;
import view.Panels.GraphicsBoard;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Player white = new Player("White", PieceColour.WHITE);
            Player black = new Player("Black", PieceColour.BLACK);

            Game game = new Game(white,black);
            GameController controller = new GameController(game);
            //, new GraphicsBoard(game.getBoard(), 85)
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