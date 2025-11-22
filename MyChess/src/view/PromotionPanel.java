package view;

import model.PieceColour;
import model.PieceType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PromotionPanel extends JPanel {

    private final PieceColour colour;
    private final int tileSize;

    private final PieceType[] options = {
            PieceType.QUEEN,
            PieceType.ROOK,
            PieceType.BISHOP,
            PieceType.KNIGHT
    };

    public PromotionPanel(PieceColour colour, int tileSize, PromotionDialog dialog) {
        this.colour = colour;
        this.tileSize = tileSize;

        int height = options.length * tileSize;

        setPreferredSize(new Dimension(tileSize, height));
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = e.getY() / tileSize;
                if (index >= 0 && index < options.length) {
                    dialog.setSelectedType(options[index]);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        int w = getWidth();
        int h = getHeight();

        // plain white background “card”
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);

        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);

        // draw each piece, first one at (0,0), others directly below
        for (int i = 0; i < options.length; i++) {
            int x = 0;
            int y = i * tileSize;

            Image sprite = SpriteSheet.getSprite(options[i], colour);
            if (sprite != null) {
                g2d.drawImage(sprite, x, y, tileSize, tileSize, null);
            }
        }

        g2d.dispose();
    }
}