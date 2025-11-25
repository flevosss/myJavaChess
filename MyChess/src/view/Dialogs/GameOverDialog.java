package view.Dialogs;

import javax.swing.*;
import java.awt.*;

public class GameOverDialog extends JDialog {

    public GameOverDialog(Window parent, String message) {
        super(parent, "Game over", ModalityType.APPLICATION_MODAL);

        JTextArea area = new JTextArea(message);
        area.setEditable(false);
        area.setOpaque(false);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton ok = new JButton("OK");
        ok.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(ok);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(area, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setResizable(false);
    }
}
