package view;

import model.PieceColour;
import model.PieceType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class SpriteSheet {
    private static final BufferedImage sheet;
    private static final BufferedImage[][] sprites;

    static {
        try {
            sheet = ImageIO.read(Objects.requireNonNull(SpriteSheet.class.getResourceAsStream("/pieces.png")));
            int tileW = sheet.getWidth() / 6;
            int tileH = sheet.getHeight() / 2;

            sprites = new BufferedImage[2][6];

            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 6; col++) {
                    sprites[row][col] = sheet.getSubimage(
                            col * tileW,
                            row * tileH,
                            tileW, tileH
                    );
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load pieces.png", e);
        }
    }

    public static Image getSprite(PieceType type, PieceColour colour) {
        int row = (colour == PieceColour.WHITE) ? 0 : 1;

        int col = switch (type) {
            case KING   -> 0;
            case QUEEN  -> 1;
            case ROOK   -> 4;
            case BISHOP -> 2;
            case KNIGHT -> 3;
            case PAWN   -> 5;
            default     -> -1;
        };

        if (col == -1) return null;
        return sprites[row][col];
    }
}
