import view.Panels.MainPanel;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Chess by fl3v0s");
            window.setResizable(false);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            MainPanel mainPanel = new MainPanel(700,1200);
            window.add(mainPanel);

            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
}
