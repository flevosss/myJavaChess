import controller.Game;
import model.PieceColour;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/icon.png")));
        frame.setIconImage(icon.getImage());

        frame.getContentPane().setBackground(Color.black);

        frame.setLayout(new GridBagLayout());

        frame.setMinimumSize(new Dimension(1000, 1000));
        frame.setLocationRelativeTo(null);

        Player player1 = new Player("mike", PieceColour.WHITE);
        Player player2 = new Player("zoe", PieceColour.BLACK);

        Game game = new Game(player1, player2);

        frame.add(game.getView());

        frame.setVisible(true);
    }
}