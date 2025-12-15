package view.Panels;

import model.Board;
import model.Piece;
import model.PieceType;
import view.SpriteSheet;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GraphicsBoard extends JPanel {

    private final int tileSize;
    private final Board board;

    private Piece draggingPiece;
    private int dragX, dragY;

    private List<Point> highlightSquares = new ArrayList<>();
    private Point selectedSquare;

    private boolean flipped = false;

    public GraphicsBoard(Board board, int tileSize) {
        this.board = board;
        this.tileSize = tileSize;

        setPreferredSize(new Dimension(
                board.getColumns() * tileSize,
                board.getRows() * tileSize
        ));
    }

    public Point screenToBoard(int x, int y) {
        int col = x / tileSize;
        int row = y / tileSize;

        if (flipped) {
            col = board.getColumns() - 1 - col;
            row = board.getRows() - 1 - row;
        }
        return new Point(col, row);
    }

    public void startDragging(Piece piece, int x, int y) {
        draggingPiece = piece;
        dragX = x;
        dragY = y;
        repaint();
    }

    public void updateDragging(int x, int y) {
        dragX = x;
        dragY = y;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        //Tiles
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getColumns(); col++) {

                int vRow = viewRow(row);
                int vCol = viewCol(col);

                g2d.setColor((row + col) % 2 == 1
                        ? new Color(82, 141, 149)
                        : new Color(235, 236, 208));

                g2d.fillRect(
                        vCol * tileSize,
                        vRow * tileSize,
                        tileSize,
                        tileSize
                );
            }
        }

        drawSelectedSquare(g2d);
        drawHighlights(g2d);

        //Pieces
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getColumns(); col++) {

                Piece piece = board.getPiece(row, col);
                if (piece.getType() == PieceType.EMPTY) continue;
                if (piece == draggingPiece) continue;

                int vRow = viewRow(row);
                int vCol = viewCol(col);

                Image sprite = SpriteSheet.getSprite(
                        piece.getType(),
                        piece.getColour()
                );

                g2d.drawImage(
                        sprite,
                        vCol * tileSize,
                        vRow * tileSize,
                        tileSize,
                        tileSize,
                        null
                );
            }
        }

        // Dragging piece on top
        if (draggingPiece != null) {
            Image sprite = SpriteSheet.getSprite(
                    draggingPiece.getType(),
                    draggingPiece.getColour()
            );

            g2d.drawImage(
                    sprite,
                    dragX - tileSize / 2,
                    dragY - tileSize / 2,
                    tileSize,
                    tileSize,
                    null
            );
        }
    }

    private void drawSelectedSquare(Graphics2D g2d) {
        if (selectedSquare == null) return;

        int row = selectedSquare.y;
        int col = selectedSquare.x;

        int vRow = viewRow(row);
        int vCol = viewCol(col);

        g2d.setColor(new Color(255, 255, 100, 80));
        g2d.fillRect(vCol * tileSize, vRow * tileSize, tileSize, tileSize);
        g2d.setColor(new Color(255, 255, 0, 200));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(vCol * tileSize, vRow * tileSize, tileSize - 1, tileSize - 1);
    }

    private void drawHighlights(Graphics2D g2d) {
        Color dotColor = new Color(120, 120, 120, 180);
        int radius = tileSize / 6;

        for (Point p : highlightSquares) {
            int row = p.y;
            int col = p.x;

            int vRow = viewRow(row);
            int vCol = viewCol(col);

            int centerX = vCol * tileSize + tileSize / 2;
            int centerY = vRow * tileSize + tileSize / 2;

            Piece targetPiece = board.getPiece(row, col);

            if (targetPiece.getType() == PieceType.EMPTY) {
                g2d.setColor(dotColor);
                g2d.fillOval(
                        centerX - radius,
                        centerY - radius,
                        radius * 2,
                        radius * 2
                );
            } else {
                int inset = tileSize / 20;
                int ringThickness = tileSize / 11;

                Graphics2D g2 = (Graphics2D) g2d.create();
                g2.setColor(new Color(50, 50, 50, 60));
                g2.setStroke(new BasicStroke(ringThickness));
                g2.drawOval(
                        vCol * tileSize + inset,
                        vRow * tileSize + inset,
                        tileSize - inset * 2,
                        tileSize - inset * 2
                );
                g2.dispose();
            }
        }
    }

    public void stopDragging() {
        draggingPiece = null;
        repaint();
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
        repaint();
    }

    private int viewRow(int boardRow) {
        return flipped ? board.getRows() - 1 - boardRow : boardRow;
    }

    private int viewCol(int boardCol) {
        return flipped ? board.getColumns() - 1 - boardCol : boardCol;
    }

    public void setHighlightSquares(List<Point> squares) {
        highlightSquares = squares;
        repaint();
    }

    public void clearHighlightSquares() {
        highlightSquares.clear();
        repaint();
    }

    public void setSelectedSquare(Point square) {
        selectedSquare = square;
        repaint();
    }

    public void clearSelectedSquare() {
        selectedSquare = null;
        repaint();
    }

    public int getTileSize() {
        return tileSize;
    }
}
