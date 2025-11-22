package view;

import model.PieceColour;
import model.PieceType;

import javax.swing.*;
import java.awt.*;

public class PromotionDialog extends JDialog {

    private PieceType selectedType;

    public PromotionDialog(Window parent, PieceColour colour, int tileSize) {
        super(parent, "Promote pawn", ModalityType.APPLICATION_MODAL); //pause the game until it closes

        setUndecorated(true); //todo: ask again why?

        PromotionPanel panel = new PromotionPanel(colour, tileSize, this);
        setContentPane(panel);
        pack();
    }

    public PieceType selectPieceType() {
        setVisible(true);   //sets this modal to true so the game is paused
        return selectedType;
    }

    void setSelectedType(PieceType type) {
        this.selectedType = type;
        dispose();
    }
}
