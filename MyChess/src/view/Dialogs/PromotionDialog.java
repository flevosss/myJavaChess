package view.Dialogs;

import model.PieceColour;
import model.PieceType;
import view.Panels.PromotionPanel;

import javax.swing.*;
import java.awt.*;

public class PromotionDialog extends JDialog {

    private PieceType selectedType;

    public PromotionDialog(Window parent, PieceColour colour, int tileSize) {
        super(parent, "Promote pawn", ModalityType.APPLICATION_MODAL); //pause the game until it closes

        setUndecorated(true);

        PromotionPanel panel = new PromotionPanel(colour, tileSize, this);
        setContentPane(panel);
        pack();
    }

    public PieceType selectPieceType() {
        setVisible(true);   //sets this modal to true so the game is paused
        return selectedType;
    }

    public void setSelectedType(PieceType type) {
        this.selectedType = type;
        dispose();
    }
}
