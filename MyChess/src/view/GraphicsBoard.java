package view;

import model.Board;
import model.Piece;
import model.PieceType;

import javax.swing.*;
import java.awt.*;

/**
 *  String white  = "#ebecd0";
 *     String green = "#739552";
 *     String greenSelected = "#a3d160";
 *     String blackMilitary = "#464341";
 *     String whiteMilitary = "#fff";
 */
public class GraphicsBoard extends JPanel {

    private final int tileSize;
    private final Board board;

    private Piece draggingPiece;
    private int dragX, dragY;

    public GraphicsBoard(Board board, int tileSize){
        this.tileSize = tileSize;
        this.setPreferredSize(
                new Dimension(
                board.getColumns() * tileSize, board.getRows() * tileSize
                ));
        this.board = board;
    }

    public void startDragging(Piece piece, int x, int y) {
        this.draggingPiece = piece;
        this.dragX = x;
        this.dragY = y;
        repaint();
    }

    public void updateDragging(int x, int y) {
        this.dragX = x;
        this.dragY = y;
        repaint();
    }


    public void stopDragging() {
        this.draggingPiece = null;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;


        for (int row = 0; row < board.getRows(); row++) {
            for (int column = 0; column < board.getColumns(); column++) {
                //first paint the tiles
                g2d.setColor((column+row) % 2 == 1 ? new Color(82, 141, 149) : new Color(235, 236, 208));
                g2d.fillRect(column*tileSize, row*tileSize,tileSize,tileSize);

                //now we draw the piece
                Piece piece = board.getPiece(row, column);
                if (piece.getType() != PieceType.EMPTY) {

                    if (piece == draggingPiece) {
                        continue;
                    }

                    Image sprite = SpriteSheet.getSprite(
                            piece.getType(),
                            piece.getColour()
                    );

                    g2d.drawImage(
                            sprite,
                            column * tileSize,
                            row * tileSize,
                            tileSize,
                            tileSize,
                            null
                    );
                }
            }
            if (draggingPiece != null) {
                Image sprite = SpriteSheet.getSprite(
                        draggingPiece.getType(),
                        draggingPiece.getColour()
                );

                int x = dragX - tileSize / 2;
                int y = dragY - tileSize / 2;

                g2d.drawImage(
                        sprite,
                        x, y,
                        tileSize,
                        tileSize,
                        null
                );
            }
        }
    }

    public int getTileSize(){
        return this.tileSize;
    }
}
