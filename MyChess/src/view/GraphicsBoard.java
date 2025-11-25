package view;

import model.Board;
import model.Piece;
import model.PieceType;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
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

    private List<Point> highlightSquares;

    public GraphicsBoard(Board board, int tileSize){
        this.tileSize = tileSize;
        this.setPreferredSize(
                new Dimension(
                board.getColumns() * tileSize,
                board.getRows() * tileSize
                ));
        this.board = board;
        highlightSquares = new ArrayList<>();
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
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        //paint the tiles
        for (int row = 0; row < board.getRows(); row++) {
            for (int column = 0; column < board.getColumns(); column++) {
                g2d.setColor((column + row) % 2 == 1
                        ? new Color(82, 141, 149)
                        : new Color(235, 236, 208));
                g2d.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
            }
        }

        //dots and rings
        drawHighlights(g2d);

        //pieces
        for (int row = 0; row < board.getRows(); row++) {
            for (int column = 0; column < board.getColumns(); column++) {
                Piece piece = board.getPiece(row, column);
                if (piece.getType() == PieceType.EMPTY) continue;
                if (piece == draggingPiece) continue;

                Image sprite = SpriteSheet.getSprite(piece.getType(), piece.getColour());
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

        //dragging on top
        if (draggingPiece != null) {
            Image sprite = SpriteSheet.getSprite(
                    draggingPiece.getType(),
                    draggingPiece.getColour()
            );
            int x = dragX - tileSize / 2;
            int y = dragY - tileSize / 2;
            g2d.drawImage(sprite, x, y, tileSize, tileSize, null);
        }
    }

    private void drawHighlights(Graphics2D g2d) {
        Color dotColor = new Color(120, 120, 120, 180);
        int radius = tileSize / 6;

        for (Point p : highlightSquares) {
            int col = p.x;
            int row = p.y;

            Piece targetPiece = board.getPiece(row, col);
            int centerX = col * tileSize + tileSize / 2;
            int centerY = row * tileSize + tileSize / 2;

            if (targetPiece.getType() == PieceType.EMPTY) {
                g2d.setColor(dotColor);
                g2d.fillOval(centerX - radius,
                        centerY - radius,
                        radius * 2, radius * 2
                );
            } else {
                //enemy piece
                int inset = tileSize / 20;
                int ringThickness = tileSize / 11;

                Color ringColor = new Color(50, 50, 50, 60);

                Graphics2D g2 = (Graphics2D) g2d.create();
                g2.setColor(ringColor);
                g2.setStroke(new BasicStroke(ringThickness));
                g2.drawOval(
                        col * tileSize + inset,
                        row * tileSize + inset,
                        tileSize - inset * 2,
                        tileSize - inset * 2
                );
                g2.dispose();
            }
        }
    }

    public void setHighlightSquares(List<Point> squares) {
        this.highlightSquares = squares;
        repaint();
    }

    public void clearHighlightSquares() {
        this.highlightSquares.clear();
        repaint();
    }

    public int getTileSize() {
        return this.tileSize;
    }
}
