import model.piece.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ChessPanel extends JPanel implements Runnable {

    final int FPS = 60;
    final int TILE_SIZE = 70;
    final int WIDTH = 8;
    final int HEIGHT = 8;

    ArrayList<int[][]> validMoves;
    WebSocketConnection webSocketConnection;

    public ChessPanel() {
        setFocusable(true);
        requestFocus();
    }

    public void paintComponent(Graphics g) {
        Image offscreenImage = createImage(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        Graphics offscreenGraphics = offscreenImage.getGraphics();
        drawBoard((Graphics2D) offscreenGraphics);
        drawPieces((Graphics2D) offscreenGraphics);
        drawValidMoves((Graphics2D) offscreenGraphics);
        g.drawImage(offscreenImage, 0, 0, null);
    }

    public void drawBoard(Graphics2D g) {
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                if ((row + col) % 2 == 0) {
                    g.setColor(new Color(209, 139, 71));
                } else {
                    g.setColor(new Color(255, 206, 158));
                }
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    public void drawPieces(Graphics2D g) {
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                Piece piece = webSocketConnection.game.getBoard().getPiece(col, row);
                if (piece != null) {
                    g.drawImage(piece.image, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                }
            }
        }
    }

    public void drawValidMoves(Graphics2D g) {
        if (validMoves != null) {
            g.setColor(new Color(0, 255, 0, 100));
            for (int[][] move : validMoves) {
                g.fillRect(move[0][0] * TILE_SIZE, move[0][1] * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            repaint();
            try {
                Thread.sleep(1000 / FPS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void connectToWebSocket(Game game) {
        webSocketConnection.connect();
    }

}
