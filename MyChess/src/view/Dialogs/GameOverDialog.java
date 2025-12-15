package view.Dialogs;

import javax.swing.*;
import java.awt.*;

public class GameOverDialog extends JDialog {

    public GameOverDialog(Window parent, String message) {
        super(parent, "Game Over", ModalityType.APPLICATION_MODAL);

        JLabel title = new JLabel("Game Over", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JTextArea area = new JTextArea(message);
        area.setFont(area.getFont().deriveFont(15f));
        area.setEditable(false);
        area.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton ok = new JButton("OK");
        ok.setPreferredSize(new Dimension(100, 35));
        ok.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        buttonPanel.add(ok);

        JPanel content = new JPanel(new BorderLayout());
        content.add(title, BorderLayout.NORTH);
        content.add(area, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(content);

        getRootPane().setDefaultButton(ok);
        setLocationRelativeTo(parent);
        pack();
        setMinimumSize(new Dimension(420, 260));
        setResizable(false);
    }
}
